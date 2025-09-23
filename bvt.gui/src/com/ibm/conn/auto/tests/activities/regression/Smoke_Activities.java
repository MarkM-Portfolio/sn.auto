package com.ibm.conn.auto.tests.activities.regression;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;

public class Smoke_Activities extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_Activities.class);

	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser1;
	private User testUser2;
	private User testUser3;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
	}
	
	@AfterMethod(alwaysRun=true)
	public void cleanUp() throws Exception {

		//Return Users
		cfg.getUserAllocator().checkInAllUsers();
		
	}

	/**
	 * <ul><b>Tests Steps:</b>
	 * <li><b>Step: </b>Create an Activity with a member</li>
	 * <li><b>Step: </b>Add a To Do, assign to a member, add a file, a bookmark and then create the ToDo</li>
	 * <li><b>Step: </b>Verify that the To Do has being created and expand and verify</li>
	 * <li><b>Step: </b>Logout as the first user and login as the second user</li>
	 * <li><b>Step: </b>Click on the To Do List tab and then verify that the To Do is present for the second user</li>
	 * <li><b>Step: </b>Logout as the first user and login as the third user</li>
	 * <li><b>Step: </b>Verify that the To Do does not appear for the third user</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = {"bvtcloud"})
	public void createActivityWithToDo() throws Exception{
		
					
		String activityRandomNumber = Helper.genDateBasedRandVal();
		String name = Data.getData().Start_An_Activity_InputText_Name_Data + activityRandomNumber;
		ActivityMember member1 = new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON);
		ActivityMember member2 = new ActivityMember(ActivityRole.OWNER, testUser3, ActivityMember.MemberType.PERSON);
		BaseActivity activity = new BaseActivity.Builder(name)
							.addMember(member1)
							.addMember(member2)
							.build();
								
		//Start the Test
		ui.startTest();
		
		// Load the component
		ui.loadComponent(Data.getData().ComponentActivities);
		
		// Login
		ui.login(testUser1);

		//Start an activity
		activity.create(ui);
		
		//Create New entry for activity created above
		String toDoRandomNumber = Helper.genDateBasedRandVal();
		String toDoTitle = Data.getData().ToDo_InputText_Title_Data + toDoRandomNumber;
		String toDoTags = Data.getData().ToDo_InputText_Tags_Data + toDoRandomNumber;
		String file = "Desert.jpg";
		String bookmarkTitle = toDoRandomNumber + "ActivitiesHome";
		String bookmarkUrl = driver.getCurrentUrl().replace("http://", "") + "activities";
		String toDoDescription = Data.getData().commonDescription + toDoRandomNumber;
		
		BaseActivityToDo toDo = BaseActivityToDo.builder(toDoTitle).tags(toDoTags)
				.assignTo(testUser2)
				.addFile(file)
				.bookmark(bookmarkTitle, bookmarkUrl)
				.description(toDoDescription)
				.dateRandom()
				.build();
		
		log.info("INFO: Add a new todo");
		toDo.create(ui);
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Click on entry
		ui.clickLink(ui.getAssignedTodoByName(toDo));
		
		//Verify entry info
		verifyToDoInfo(toDo);
		
		//Logout as current user
		ui.logout();
		
		//login as the second user
		ui.login(testUser2);
		
		//check that the activity/todo is present
		ui.clickLink(ActivitiesUIConstants.ToDoListTab);

		ui.isTextPresent(activityRandomNumber);
		ui.isTextPresent(toDo.getTitle());
		//Select the ToDo and expand it and then verify the contents of the ToDo
		ui.clickLink(ui.getAssignedTodoByName(toDo));

		verifyToDoInfo(toDo);
		
		//Logout as current user
		ui.logout();
		
		//login as the second user
		ui.login(testUser3);
		
		//check that the activity/todo is present
		ui.clickLink(ActivitiesUIConstants.ToDoListTab);

		ui.isTextPresent(activityRandomNumber);
		driver.isTextNotPresent(toDo.getTitle());
		
		//End of test
		ui.endTest();
		
	}
	
	/**
	 * <ul><b>Tests Steps:</b>
	 * <li><b>Step: </b>Create an Activity with a member</li>
	 * <li><b>Step: </b>Add a To Do, assign to a member, add a file, a bookmark and then create the ToDo</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = {"bvtcloud"})
	public void checkToDoNotification() throws Exception{
		/**
		 * This is an empty test currently as the email notification that should be tested here is 
		 * too unstable for this test currently
		 * 
		 * Liam Walsh is looking into other options for this test now - please contact him for further update
		 * 
		 */
	}

	/**
	 * verifyToDoInfo - 
	 * @param toDo
	 */
	public void verifyToDoInfo(BaseActivityToDo toDo) {
		Assert.assertTrue(driver.isTextPresent(toDo.getTitle()), "Title for ToDo is missing");
		if(toDo.getTags() != null)
			Assert.assertTrue(driver.isTextPresent(toDo.getTags()), "Tags for ToDo is missing");
		if(toDo.getDescription() != null)
			Assert.assertTrue(driver.isTextPresent(toDo.getDescription()), "Description for ToDo is missing");
		if(!toDo.getFiles().isEmpty()) {
			for(String fileName: toDo.getFiles())
				Assert.assertTrue(driver.isTextPresent(fileName), fileName + " file for ToDo is missing");
		}
		if(toDo.getDueDateRandom() || toDo.getDueDate() != null) {
			Assert.assertTrue(driver.isTextPresent("Due "), "Due date for ToDo is missing");
		}
	}
	
}