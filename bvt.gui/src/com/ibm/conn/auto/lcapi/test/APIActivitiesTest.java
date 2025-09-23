package com.ibm.conn.auto.lcapi.test;

import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;

public class APIActivitiesTest extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(APIActivitiesTest.class);
	private boolean isOnPremTest;
	private TestConfigCustom cfg;	
	private User testUser, testUser2;
	private String testURL;
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		testURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		config = new ServiceConfig(client, testURL, true);
		
		ServiceEntry activities = config.getService("activities");
		assert(activities != null);

		Utils.addServiceAdminCredentials(activities, client);
		
		// Set whether this test is being run OnPrem or on SmartCloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremTest = true;
		} else {
			isOnPremTest = false;
		}
				
	}

	@Test (groups = {"apitest"})
	public void createEntryMention(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIActivitiesHandler apiHandler = new APIActivitiesHandler(cfg.getProductName(), testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		log.info("INFO: API user to mention: " + testUser2.getDisplayName());
		
		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();
		
		BaseActivity baseActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
													.dueDateRandom()
													.useCalPick(true)
													.goal(Data.getData().commonDescription + testName)
													.isPublic(true)
													.build();
		
		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create activity using API");
		Activity newActivity = apiHandler.createActivity(baseActivity, isOnPremTest);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating entry with mention to " + testUser2.getDisplayName() + " using API");
		ActivityEntry result = apiHandler.addMention_ActivityEntry(newActivity, false, mentions);

		assert result != null: "Creation of activity mentions failed";
				
	}

	@Test (groups = {"apitest"})
	public void createEntryCommentMention(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIActivitiesHandler apiHandler = new APIActivitiesHandler(cfg.getProductName(), testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		log.info("INFO: API user to mention: " + testUser2.getDisplayName());

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();
		
		BaseActivity baseActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
													.dueDateRandom()
													.useCalPick(true)
													.goal(Data.getData().commonDescription + testName)
													.isPublic(true)
													.build();

		BaseActivityEntry entry = BaseActivityEntry.builder(testName + Helper.genDateBasedRandVal())
								 .tags(Helper.genDateBasedRandVal())
								 .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
								 .build();
		
		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create activity using API");
		Activity newActivity = apiHandler.createActivity(baseActivity, isOnPremTest);

		log.info("INFO: " + testUser.getDisplayName() + " creating entry (API)");
		ActivityEntry activityEntry = apiHandler.createActivityEntry(Data.getData().commonName + Helper.genDateBasedRand() , entry.getDescription(), entry.getTags(), newActivity, false);

		log.info("INFO: " + testUser.getDisplayName() + " creating Entry comment with mention to " + testUser2.getDisplayName() + " using API");
		Reply result = apiHandler.addMention_EntryReply(activityEntry, false, mentions);

		assert result != null: "Creation of activity mentions failed";
				
	}

	@Test (groups = {"apitest"})
	public void createTodoMention(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIActivitiesHandler apiHandler = new APIActivitiesHandler(cfg.getProductName(), testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		log.info("INFO: API user to mention: " + testUser2.getDisplayName());

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();
		
		BaseActivity baseActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
													.dueDateRandom()
													.useCalPick(true)
													.goal(Data.getData().commonDescription + testName)
													.isPublic(true)
													.build();

		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create activity using API");
		Activity newActivity = apiHandler.createActivity(baseActivity, isOnPremTest);

		log.info("INFO: " + testUser.getDisplayName() + " creating ToDo with mention to " + testUser2.getDisplayName() + " using API");
		Todo result = apiHandler.addMention_Todo(newActivity, false, mentions);
		
		assert result != null: "Creation of activity mentions failed";
				
	}

	@Test (groups = {"apitest"})
	public void createTodoCommentMention(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIActivitiesHandler apiHandler = new APIActivitiesHandler(cfg.getProductName(), testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		log.info("INFO: API user to mention: " + testUser2.getDisplayName());

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();
		
		BaseActivity baseActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
													.dueDateRandom()
													.useCalPick(true)
													.goal(Data.getData().commonDescription + testName)
													.isPublic(true)
													.build();

		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + Helper.genDateBasedRandVal())
							   .tags(Helper.genDateBasedRandVal())
							   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
							   .build();

		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create activity using API");
		Activity newActivity = apiHandler.createActivity(baseActivity, isOnPremTest);

		log.info("INFO: " + testUser.getDisplayName() + " creating ToDo using API");
		Todo newTodo = apiHandler.createActivityTodo(testName + Helper.genDateBasedRand(), toDo.getDescription(), toDo.getTags(), newActivity, false);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating ToDo Reply with mention to " + testUser2.getDisplayName() + " using API");
		Reply result = apiHandler.addMention_TodoReply(newTodo, false, mentions);
		
		assert result != null: "Creation of activity mentions failed";
				
	}

	@Test (groups = {"apitest"})
	public void deleteEntryComment(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIActivitiesHandler apiHandler = new APIActivitiesHandler(cfg.getProductName(), testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		BaseActivity baseActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
													.dueDateRandom()
													.useCalPick(true)
													.goal(Data.getData().commonDescription + testName)
													.isPublic(true)
													.build();

		BaseActivityEntry entry = BaseActivityEntry.builder(testName + Helper.genDateBasedRandVal())
								 .tags(Helper.genDateBasedRandVal())
								 .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
								 .build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create activity using API");
		Activity newActivity = apiHandler.createActivity(baseActivity, isOnPremTest);

		entry.setParent(newActivity);

		log.info("INFO: " + testUser.getDisplayName() + " creating entry (API)");
		ActivityEntry activityEntry = apiHandler.createActivityEntry(Data.getData().commonName + Helper.genDateBasedRand() , entry.getDescription(), entry.getTags(), newActivity, false);

		String activityReplyContent = Data.getData().StatusComment + Helper.genMonthDateBasedRandVal();
		
		log.info("INFO: " + testUser.getDisplayName() + " creating Entry comment using API");
		Reply reply = apiHandler.createActivityEntryReply(newActivity, activityEntry, activityReplyContent, false);

		log.info("INFO: " + testUser.getDisplayName() + " deleting Entry comment using API");
		boolean deleted = apiHandler.deleteEntryComment(reply);

		assert deleted == true: "Deletion of activity entry comment failed";		
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateActivity_PublicActivity() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIActivitiesHandler activityOwner = new APIActivitiesHandler(cfg.getProductName(), testURL,
																		testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a public activity using the API");
		BaseActivity baseActivity = new BaseActivity.Builder(testName)
													.goal(Data.getData().commonDescription + Helper.genStrongRand())
													.shareExternal(false)
													.isPublic(true)
													.build();
		Activity publicActivity = activityOwner.createActivity(baseActivity, isOnPremTest);
		
		assert publicActivity != null : "ERROR: There was a problem with creating the public activity using the API";
		
		log.info("INFO: Performing clean-up now that the API test has completed");
		activityOwner.deleteActivity(publicActivity);
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateActivity_PrivateActivity() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIActivitiesHandler activityOwner = new APIActivitiesHandler(cfg.getProductName(), testURL,
																		testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a public activity using the API");
		BaseActivity baseActivity = new BaseActivity.Builder(testName)
													.goal(Data.getData().commonDescription + Helper.genStrongRand())
													.shareExternal(false)
													.isPublic(false)
													.build();
		Activity privateActivity = activityOwner.createActivity(baseActivity, isOnPremTest);
		
		assert privateActivity != null : "ERROR: There was a problem with creating the private activity using the API";
		
		log.info("INFO: Performing clean-up now that the API test has completed");
		activityOwner.deleteActivity(privateActivity);
	}
	
	@Test(groups = {"apitest"})
	public void test_AssignToDoItemToUser() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIProfilesHandler testUserProfile = new APIProfilesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		APIActivitiesHandler activityOwner = new APIActivitiesHandler(cfg.getProductName(), testURL,
																		testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a public activity using the API");
		BaseActivity baseActivity = new BaseActivity.Builder(testName)
													.goal(Data.getData().commonDescription + Helper.genStrongRand())
													.shareExternal(false)
													.isPublic(true)
													.build();
		Activity publicActivity = activityOwner.createActivity(baseActivity, isOnPremTest);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a to-do item for the activity");
		String todoTitle = Data.getData().commonName + Helper.genStrongRand();
		String todoDescription = Data.getData().commonDescription + Helper.genStrongRand();
		String todoTags = Data.getData().commonTag + Helper.genStrongRand();
		
		Todo publicToDo = activityOwner.createActivityTodo(todoTitle, todoDescription, todoTags, publicActivity, false);
		
		List<Element> listOfAssigned = publicToDo.getAssignedToElement();
		assert listOfAssigned.size() == 0 : "ERROR: After to-do item creation, the list of assigned users was not empty as expected";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now assign themselves the to-do item");
		publicToDo = activityOwner.assignToDoItemToUser(publicToDo, testUserProfile);
		
		assert publicToDo != null : "ERROR: There was a problem with assigning a user to the to-do item using the API";
		
		listOfAssigned = publicToDo.getAssignedToElement();
		assert listOfAssigned.size() == 1 : "ERROR: After assigning the to-do item to a user, the list of assigned users did not contain a single item as expected";
		
		Element assignedUser = listOfAssigned.get(0);
		assert assignedUser.getAttributeValue("name").equals(testUserProfile.getDesplayName()) == true : 
				"ERROR: The name assigned to the to-do item does not match the users display name as expected";
		assert assignedUser.getAttributeValue("userid").equals(testUserProfile.getUUID()) == true :
				"ERROR: The user ID assigned to the to-do item does not match the users own user ID as expected";
		
		log.info("INFO: Performing clean-up now that the API test has completed");
		activityOwner.deleteActivity(publicActivity);
	}
	
	@Test(groups = {"apitest"})
	public void test_AssignToDoItemToAnyone() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIActivitiesHandler activityOwner = new APIActivitiesHandler(cfg.getProductName(), testURL,
																		testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a public activity using the API");
		BaseActivity baseActivity = new BaseActivity.Builder(testName)
													.goal(Data.getData().commonDescription + Helper.genStrongRand())
													.shareExternal(false)
													.isPublic(true)
													.build();
		Activity publicActivity = activityOwner.createActivity(baseActivity, isOnPremTest);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a to-do item for the activity");
		String todoTitle = Data.getData().commonName + Helper.genStrongRand();
		String todoDescription = Data.getData().commonDescription + Helper.genStrongRand();
		String todoTags = Data.getData().commonTag + Helper.genStrongRand();
		
		Todo publicToDo = activityOwner.createActivityTodo(todoTitle, todoDescription, todoTags, publicActivity, false);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now assign anyone to the to-do item");
		publicToDo = activityOwner.assignToDoItemToAnyone(publicToDo);
		
		assert publicToDo != null : "ERROR: There was an error with assigning anyone to the to-do item using the API";
		
		List<Element> listOfAssigned = publicToDo.getAssignedToElement();
		assert listOfAssigned.size() == 0 : "ERROR: After assigning the to-do item to anyone, the list of assigned users was not cleared as expected";
		
		log.info("INFO: Performing clean-up now that the API test has completed");
		activityOwner.deleteActivity(publicActivity);
	}

	@Test(groups = {"apitest"})
	public void test_MarkToDoItemAsCompleteOrIncomplete() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIProfilesHandler testUserProfile = new APIProfilesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		APIActivitiesHandler activityOwner = new APIActivitiesHandler(cfg.getProductName(), testURL,
																		testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a public activity using the API");
		BaseActivity baseActivity = new BaseActivity.Builder(testName)
													.goal(Data.getData().commonDescription + Helper.genStrongRand())
													.shareExternal(false)
													.isPublic(true)
													.build();
		Activity publicActivity = activityOwner.createActivity(baseActivity, isOnPremTest);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a to-do item for the activity");
		String todoTitle = Data.getData().commonName + Helper.genStrongRand();
		String todoDescription = Data.getData().commonDescription + Helper.genStrongRand();
		String todoTags = Data.getData().commonTag + Helper.genStrongRand();
		
		Todo publicToDo = activityOwner.createActivityTodo(todoTitle, todoDescription, todoTags, publicActivity, false);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now assign themselves the to-do item");
		publicToDo = activityOwner.assignToDoItemToUser(publicToDo, testUserProfile);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now mark the to-do item as completed");
		publicToDo = activityOwner.markToDoItemAsCompleteOrIncomplete(publicToDo, true);
		
		assert publicToDo != null : "ERROR: There was a problem with marking the to-do item as complete using the API";
		
		String categoryString = publicToDo.getIsCompleteCategory().toString();
		
		assert publicToDo.isComplete() == true : "ERROR: The to-do item was not marked as completed as expected";
		assert categoryString.indexOf("term=\"completed\"") > -1 : "ERROR: The to-do entry category was not marked as completed as expected";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now mark the to-do item as incomplete (re-open the to-do item");
		publicToDo = activityOwner.markToDoItemAsCompleteOrIncomplete(publicToDo, false);
		
		assert publicToDo != null : "ERROR: There was a problem with re-opening the to-do item using the API";
		
		assert publicToDo.isComplete() == false : "ERROR: The to-do item was not marked as re-opened (incomplete) as expected";
		assert publicToDo.getIsCompleteCategory() == null : "ERROR: The to-do entry category was not marked as null (incomplete / re-opened) as expected";
		
		log.info("INFO: Performing clean-up now that the API test has completed");
		activityOwner.deleteActivity(publicActivity);
	}
	
	@Test(groups = {"apitest"})
	public void test_DeleteToDoItem() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIActivitiesHandler activityOwner = new APIActivitiesHandler(cfg.getProductName(), testURL,
																		testUser.getAttribute(cfg.getLoginPreference()),
																		testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a public activity using the API");
		BaseActivity baseActivity = new BaseActivity.Builder(testName)
													.goal(Data.getData().commonDescription + Helper.genStrongRand())
													.shareExternal(false)
													.isPublic(true)
													.build();
		Activity publicActivity = activityOwner.createActivity(baseActivity, isOnPremTest);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a to-do item for the activity");
		String todoTitle = Data.getData().commonName + Helper.genStrongRand();
		String todoDescription = Data.getData().commonDescription + Helper.genStrongRand();
		String todoTags = Data.getData().commonTag + Helper.genStrongRand();
		
		Todo publicToDo = activityOwner.createActivityTodo(todoTitle, todoDescription, todoTags, publicActivity, false);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the to-do item");
		boolean deleted = activityOwner.deleteToDoItem(publicToDo);
		
		assert deleted == true : "ERROR: The to-do item was not deleted as expected";
		
		log.info("INFO: Performing clean-up now that the API test has completed");
		activityOwner.deleteActivity(publicActivity);
	}
	
	@Test(groups = {"apitest"})
	public void test_NotifyUserAboutActivityEntry() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIActivitiesHandler activitiesAPIUser1 = new APIActivitiesHandler(cfg.getProductName(), testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		APIProfilesHandler profilesAPIUser2 = new APIProfilesHandler(testURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// Create a public activity
		BaseActivity baseActivity = new BaseActivity.Builder(testName + Helper.genStrongRand())
													.goal(Data.getData().commonDescription + Helper.genStrongRand())
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.shareExternal(false)
													.isPublic(true)
													.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public activity using the API");
		Activity activity = activitiesAPIUser1.createActivity(baseActivity, isOnPremTest);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the activity as a member");
		activitiesAPIUser1.addMemberToActivity(activity, testUser2);

		// Add an entry to the public activity
		BaseActivityEntry baseActivityEntry = BaseActivityEntry.builder(testName + Helper.genStrongRand())
																.tags(Data.getData().commonTag + Helper.genStrongRand())
																.description(Data.getData().commonDescription + Helper.genStrongRand())
																.build();
		baseActivityEntry.setParent(activity);
		baseActivityEntry.setMarkPrivate(false);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create an entry in the public activity using the API");
		ActivityEntry activityEntry = baseActivityEntry.createEntryAPI(activitiesAPIUser1);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now notify " + testUser2.getDisplayName() + " about the activity entry");
		boolean notificationSent = activitiesAPIUser1.notifyUserAboutActivityEntry(activity, activityEntry, profilesAPIUser2);
		
		assert notificationSent == true : "ERROR: There was a problem with sending a notification to an activity member using the API";
		
		log.info("INFO: Performing clean-up now that the API test has completed");
		activitiesAPIUser1.deleteActivity(activity);
	}
}
