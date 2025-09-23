package com.ibm.conn.auto.tests.activities.regression;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityTemplate;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.ActivitiesUI;

public class ActivitiesRegressionCleanupPhase1 extends SetUpMethods2{

private static Logger log = LoggerFactory.getLogger(ActivitiesRegressionCleanupPhase1.class);
private ActivitiesUI ui;
private TestConfigCustom cfg;
private User testUser, testLookAheadUser,testUser1,testUser2;
protected APIActivitiesHandler apiOwner;
private boolean isOnPremise;
private String serverURL;


/*
 * Phase 1 of regression test cleanup work
 * Passing tests from the current Activities regression suite have been copied into this file.
 * As failing regression tests get fixed, they will be moved into this file.
 * This file will become the new regression suite.
 * 
 * NOTE: These test methods may also need some additional cleanup work...Phase 2 of cleanup work
 * ie: remove code comments and replace with info.log, add cleanup/delete entry steps, cleanup css & create
 * new selectors in common repository etc...
 */	

@BeforeClass(alwaysRun=true)
public void setUpClass() {

	cfg = TestConfigCustom.getInstance();
	testUser = cfg.getUserAllocator().getUser();			
	testUser1 = cfg.getUserAllocator().getUser();
	testUser2 = cfg.getUserAllocator().getUser();
	testLookAheadUser = cfg.getUserAllocator().getUser();
	
	serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
}


@BeforeMethod(alwaysRun=true)
public void setUp() {
	
	//initialize the configuration
	cfg = TestConfigCustom.getInstance();
	ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
	if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
		isOnPremise = true;
	} else {
		isOnPremise = false;
	}
}

		
	/**
	 * <ul>
	 * <li><B>Info:</B>Create Activity Template</li>
	 * <li><B>Step:</B>Log into Activities</li>
	 * <li><B>Step:</B>Create an Activity template</li>
	 * <li><B>Step:</B>Click the option to 'Start an Activity from this template' button</li>
	 * <li><B>Verify:</B>The text in the Description field</li>
	 * <li><B>Verify:</B>The text in the Tags field</li>
	 * <li><B>Step:</B>Click on the Save button</li>
	 * <li><B>Step:</B>Click on the 'More' link for the activity template</li>
	 * <li><B>Step:</B>Click on the 'Start an Activity from this template' link</li>
	 * <li><B>Verify:</B>The text in the Description field</li>
	 * <li><B>Verify:</B>The text in the Tags field</li>
	 * <li><B>Cleanup:</B> Delete the activity & activity template</li>
	 * </ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void createActivityFromTemplate() throws Exception {
		String testName = ui.startTest();
		
		BaseActivityTemplate template = new BaseActivityTemplate.Builder(testName + Helper.genDateBasedRand())
	    														.tags(Data.getData().Start_An_Activity_Template_InputText_Tags_Data + Helper.genDateBasedRand())
	    														.description("Description for " + testName + Helper.genDateBasedRand())
	    														.addMember(new ActivityMember(ActivityRole.OWNER, testLookAheadUser, ActivityMember.MemberType.PERSON))
	    														.build();
		
		String templateName=(template.getName());
		
		log.info("INFO: Log into Activities as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Create a new template");
		template.create(ui);
		
		log.info("INFO: Select start an activity from a template");
		ui.clickLinkWait(ActivitiesUIConstants.startAnActivityFromTemplate);

		log.info("INFO: Validate the template description");
		Assert.assertTrue(driver.isTextPresent(template.getDescription()), 
				"ERROR: The description for the created template did not appear.");

		log.info("INFO: Validate the template tags");
		String tags = driver.getFirstElement(ActivitiesUIConstants.Start_An_Activity_InputText_Tags).getAttribute("value");
		Assert.assertEquals(template.getTags(), tags, 
							"ERROR: The tags for the created template did not appear.");

		log.info("INFO: Select to save the template");
		ui.clickSaveButton();
		
		log.info("INFO: Wait for template name to be visible");
		ui.fluentWaitTextPresent(template.getName());

		log.info("INFO: Select create an Activity using template");
		ui.clickLinkWait(ActivitiesUIConstants.Activity_Template);
		
		log.info("INFO: Select show more info");
		ui.clickLink(ActivitiesUIConstants.showMoreInfo);
		
		log.info("INFO: Select start an activity from a template in the list");
		ui.clickLink(ActivitiesUIConstants.startAnActivityFromTemplateInList);

		log.info("INFO: Validate the description");
		Assert.assertTrue(driver.isTextPresent(template.getDescription()), 
				"ERROR: The description for the created template did not appear.");

		log.info("INFO: Validate the template tags");
		Assert.assertTrue(driver.isTextPresent(template.getTags()), 
				"ERROR: The tags for " + "the created template did not appear.");

		log.info("INFO: Cancel the activity");
		driver.typeNative(Keys.ESCAPE);
		
		log.info("INFO: Wait for template name to be visible");
		ui.fluentWaitTextPresent(template.getName());
		
		log.info("INFO: Refresh the browser window");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Cleanup: Delete the template");	
		ui.deleteTemplate(template);
		
		log.info("INFO: Cleanup: Delete activity created using the template");
		log.info("INFO: Click on the Activities link");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		log.info("INFO: Click on the activity created from a template");
		ui.clickLinkWait("link="+ templateName);
		
		log.info("INFO: Click on Activity Actions link");
		ui.clickLinkWait(ActivitiesUIConstants.activityActionMenu);
			
		log.info("INFO: From the Activity Actions menu select Delete Activity");
		ui.clickLinkWait(ActivitiesUIConstants.actionMenuDeleteActivity);
		
		log.info("INFO: Click OK on the delete activity confirmation dialog");
		ui.clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);

		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Verify Activity Template Content</li>
	 * <li><B>Step:</B>Log into Activities</li>
	 * <li><B>Step:</B>Create an Activity template</li>
	 * <li><B>Verify:</B>The text in the Description field</li>
	 * <li><B>Verify:</B>The text in the Tags field</li>
	 * <li><B>Step:</B>Click on the Members link</li>
	 * <li><B>Verify:</B>The user added as a member appears on the Members page</li>
	 * <li><B>Cleanup:</B> Delete the activity template</li>
	 * </ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void verifyTemplateData() throws Exception {
		String testName = ui.startTest();

		BaseActivityTemplate template = new BaseActivityTemplate.Builder(testName + Helper.genDateBasedRand())
																.tags(Data.getData().Start_An_Activity_Template_InputText_Tags_Data + Helper.genDateBasedRand())
																.description("Description for " + testName + Helper.genDateBasedRand())
																.addMember(new ActivityMember(ActivityRole.OWNER, testLookAheadUser, ActivityMember.MemberType.PERSON))
																.build();
		
		log.info("INFO: Log into Activities as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Create a new template");
		template.create(ui);

		log.info("INFO: Select more options");
		ui.clickLinkWait(ActivitiesUIConstants.SelectMoreOption);

		log.info("INFO: Validate the template Description");
		Assert.assertTrue(driver.isTextPresent(template.getDescription()), 
				"ERROR: The description for the created template did not appear.");

		log.info("INFO: Validate the template tags");
		Assert.assertTrue(driver.isTextPresent(template.getTags()), 
				"ERROR: The tags for the created template did not appear.");

		log.info("INFO: Select Members link");
		ui.clickLinkWait(ActivitiesUIConstants.SelectMembers);
		
		log.info("INFO: Validate user is present");
		Assert.assertTrue(driver.isTextPresent(testLookAheadUser.getDisplayName()),
				"ERROR: The member added to the template did not appear.");
		
		log.info("INFO: Click on the Activity Templates link");
		ui.clickLinkWait(ActivitiesUIConstants.Activity_Template);
		
		log.info("INFO: Cleanup: Delete the template");	
		ui.deleteTemplate(template);

		ui.endTest();
	}
	
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Verify Activity Template Text & Buttons</li>
	 * <li><B>Step:</B>Log into Activities</li>
	 * <li><B>Step:</B>Click button to create an Activity template</li>
	 * <li><B>Verify:</B>'Create a Template' header appears</li>
	 * <li><B>Verify:</B>The 'Name' field label appears</li>
	 * <li><B>Verify:</B>The 'Tag' field label appears</li>
	 * <li><B>Verify:</B>The 'Members' field label appears</li>
	 * <li><B>Verify:</B>The 'About this template' field label appears</li>
	 * </ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void verifyTemplateTextAndButtons() throws Exception {
		ui.startTest();

		log.info("INFO: Log into Activities as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Select Activity Template");
		ui.clickLink(ActivitiesUIConstants.Activity_Template);
		
		log.info("INFO: Select Create a template");
		ui.clickLink(ActivitiesUIConstants.createATemplate);

		log.info("INFO: Validate the correct fields appear");
		String[] stringsToVerify = {"Create a Template", "Name", "Tags", "Members", "About this template"};
		for (String toVerify : stringsToVerify) {
			Assert.assertTrue(driver.isTextPresent(toVerify), 
					"The text \"" + toVerify + "\" did not appear in the form for creating an activity template");
		}

		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>ToDo: Assign ToDo to a Non-Member</li>
	 * <li><B>Step:</B>Log into Activities</li>
	 * <li><B>Step:</B>Create an Activity</li>
	 * <li><B>Step:</B>Create a ToDo and add a custom 'Person' field</li>
	 * <li><B>Step:</B>Add a non-activity member to the custom 'Person' field</li>
	 * <li><B>Step:</B>Create a 2nd ToDo</li>
	 * <li><B>Step:</B>Navigate to the Assigned To field of the 2nd ToDo</li>
	 * <li><B>Verify:</B>The user added to the custom 'Person' field in the 1st ToDo does not appear as a user the ToDo can be assigned to</li>
	 * <li><B>Cleanup:</B> Delete the activity</li>	 
	 * </ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})
	public void customePersonNotInAssigneTo() {
		String testName = ui.startTest();

		User anotherPerson = testUser2;
		
		BaseActivityToDo toDo1 = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
                                                 .tags(testName + Helper.genDateBasedRandVal())
                                                 .addPerson(anotherPerson)
                                                 .build();
		
		BaseActivityToDo toDo2 = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
                                                 .tags(testName + Helper.genDateBasedRandVal())
                                                 .build();
		
		log.info("INFO: Create an activity");
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).build();

		log.info("INFO: Log into Activities as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);
		
		log.info("INFO: Create an Activity");
		activity.create(ui);
		
		log.info("INFO: Create a ToDo with a custom 'Person' field");
		ui.createToDo(toDo1);
		
		log.info("INFO: Create a 2nd ToDo");
		ui.createToDo(toDo2);

		log.info("INFO: 2nd ToDo: Display the list of users the ToDo can be assigned to");
		String toDoUUI = ui.getEntryUUID(toDo2);
		ui.editEntryLink(toDoUUI);
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		ui.getFirstVisibleElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown)
				.useAsDropdown()
				.selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));
		Element peopleList = ui.getFirstVisibleElement("css=div.peopleList");
		
		log.info("INFO: Verify the 'Person' added to the custom 'Person' field of the 1st ToDo is not listed as a user the todo can be assigned to.");
		Assert.assertTrue(ui.isTextNotPresentWithinElement(peopleList,anotherPerson.getDisplayName()),
				"Error: should not find the person " + anotherPerson.getDisplayName() + " in assignees list");
		
		log.info("INFO: Cleanup - Delete the activity");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		activity.delete(ui);
				
		ui.endTest();
	}	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Trash View - Delete Activity</li>
	 * <li><B>Step:</B>Create an Activity using the API</li>
	 * <li><B>Step:</B>Log into Activities</li>
	 * <li><B>Step:</B>Delete the Activity</li>
	 * <li><B>Step:</B>Navigate to the Trash view</li>
	 * <li><B>Verify:</B>The deleted activity appears in the Trash</li>
	 * <li><B>Step:</B>Click on the activity in the Trash</li>
	 * <li><B>Verify:</B>The activity opens without error</li>
	 * </ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})	
	public void openActivityInTrash() {

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

		log.info("INFO: Create activity using API");
		activity.createAPI(apiOwner);
		
		log.info("INFO: Log into Activities as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Delete the activity created via API");		
		activity.delete(ui);
		
		log.info("INFO: Go to the Trash folder");	
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Trash);
		
		log.info("INFO: Verify the deleted activity appears in the Trash");
		Assert.assertTrue(driver.isTextPresent(activity.getName()), 
						  "ERROR: Deleted activity not found in the Trash. Activity name: " + activity.getName());
		
		log.info("INFO: Click on the activity in the Trash");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		log.info("INFO: Verify the activity opens without error");
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()+" - Trash"),
				"ERROR: Unable to open the activity");

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Add a Related Activity</li>
	 * <li><B>Step:</B>Create two activities via the API</li>
	 * <li><B>Step:</B>Log in to Activities and open second activity</li>
	 * <li><B>Step:</B>Select Activity Actions > Add Related Activity</li>
	 * <li><B>Verify:</B>Check that Add Related Activity dialog displays</li>
	 * <li><B>Step:</B>Select the first activity from the dialog</li>
	 * <li><B>Step:</B>Select save</li>
	 * <li><B>Verify:</B>Check that the related activity displays in the entry list</li>
	 * <li><B>Step:</B>Select the entry</li>
	 * <li><B>Verify:</B>Check that the related activity displays as a bookmark within the entry</li>
	 * <li><B>Cleanup:</B>Delete the (2) activities</li>
	 * </ul>
	 */	
	@Test(groups = {"regression", "regressioncloud"} )
	public void addRelatedActivity() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity1 = new BaseActivity.Builder("First " + testName + Helper.genDateBasedRand())
								.tags(testName)
								.goal("One ACT in regression test for " + testName)
								.build();


		BaseActivity activity2 = new BaseActivity.Builder("Second " + testName + Helper.genDateBasedRandVal3())
								.tags(testName)
								.goal("Two ACT in regression test for " + testName)
								.build();

		log.info("INFO: Create activities using API");
		activity1.createAPI(apiOwner);
		activity2.createAPI(apiOwner);

		log.info("INFO: Log into Activities as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Open the 2nd activity created using the API");
		log.info("INFO: Open the activity " + activity2.getName());
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity2));

		log.info("INFO: Select Activity Actions > Add Related Activity");
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuAddRelatedActivity);
		
		log.info("INFO: Verify Add Related Activity dialog displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().AddRelatedActivity), 
				"ERROR: The dialog could not be found");
				
		log.info("INFO: Select the first activity created from the dialog & click Save");
		ui.clickLinkWait("css=label:contains('" + activity1.getName() + "')");
		ui.clickSaveButton();
		
		String selector = "css=div[class='entryIcon nodeTypeEntry lconnSprite lconnSprite-iconActivities16'] " +
				" + h4[id$='_miniTitle'] > span:contains('" + activity1.getName() + "')";
		log.info("INFO: Verify the related activity displays as an entry in the outline view");
		Assert.assertTrue(driver.isElementPresent(selector), 
				"ERROR: Related Activity " + activity1.getName() + " could not be found");
		
		log.info("INFO: Select the entry");
		ui.clickLink(selector);
		
		selector = "css=a[class='lotusBookmarkField']:contains('" + activity1.getName() + "')";
		log.info("INFO: Verify the related activity displays as a bookmark within the entry");
		Assert.assertTrue(driver.isElementPresent(selector), 
				"ERROR: " + activity1.getName() + " bookmark could not be found");
		
		log.info("INFO: Click on the Activities link");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		log.info("INFO: Cleanup - delete the communities");
		log.info("INFO: Delete the 1st activity");
		activity1.delete(ui);
		
		log.info("INFO: Delete the 2nd activity");
		activity2.delete(ui);
		
		ui.endTest();
	}

	
	/**
	 * Attempt to create two different activities. Create an entry in one and
	 * attempt to copy it to the other using "copy" in the extra actions menu
	 * under the entry. Verify that the entry appears in the other activity.
	 * @throws Exception
	 */	
	@Test(groups = {"regression", "regressioncloud"} )
	public void copyEntry() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();

		long dateRand = Long.parseLong(Helper.genDateBasedRand()) + 1;

		BaseActivity activity2 = new BaseActivity.Builder(testName + dateRand)
		                                         .tags(testName)
		                                         .goal("Goal for "+ testName)
		                                         .build();
		
		log.info("INFO: Create activities using API");
		activity.createAPI(apiOwner);
		activity2.createAPI(apiOwner);

		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Navigate to the 2nd Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity2));
				
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
				                                   .tags(Helper.genDateBasedRandVal())
				                                   .dateRandom()
				                                   .description(Data.getData().commonDescription + testName)
				                                   .build();

		log.info("INFO: Create an activity entry");
		entry.create(ui);

		log.info("INFO: Copy the entry to the 1st activity");
		ui.clickLink(ActivitiesUIConstants.More_Actions);
		driver.getFirstElement(ActivitiesUIConstants.actionCopy).click();

		ui.getFirstVisibleElement("css=label:contains('" + activity.getName() + "')").click();
		ui.clickLinkWait("css=input[value='Copy']");
		ui.fluentWaitTextNotPresent(Data.getData().CopyActivityEntry);

		log.info("INFO: Click on the Activities tab");
		ui.clickLink(ActivitiesUIConstants.Activities_Tab);

		log.info("INFO: Select the activity that the entry was copied to");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: Verify the entry appears");
		ui.fluentWaitTextPresent(entry.getTitle());

		ui.endTest();
	}
	
	/**
	 * Attempt to create an activity. Create two entries and attempt to copy 
	 * one of them to the other using "copy" in the extra actions menu
	 * under the entry. Verify that the copied entry appears multiple times 
	 * in the activity.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void copyEntryToSameActivity() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();

		long dateRand = Long.parseLong(Helper.genDateBasedRand()) + 1;

		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + dateRand)
				                                   .tags(Helper.genDateBasedRandVal())
				                                   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				                                   .build();

		dateRand++;

		BaseActivityEntry entry2 = BaseActivityEntry.builder(testName + " entry" + dateRand)
				                                    .tags(Helper.genDateBasedRandVal())
				                                    .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				                                    .build();		
		
		log.info("INFO: Create activities using API");
		activity.createAPI(apiOwner);

		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Click on the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));		

		log.info("INFO: Create entry1");
		ui.createEntry(entry);

		log.info("INFO: Create entry2");
		ui.createEntry(entry2);

		log.info("INFO: Copy entry1 to entry2");
		ui.clickLink(ActivitiesUIConstants.More_Actions);
		driver.getFirstElement(ActivitiesUIConstants.actionCopy).click();
		ui.clickLink("css=span:contains('Copy to Activity')");	//Copy to Entry
		ui.clickLink("css=label:contains('" + activity.getName() + "')");	//entryNameTwo
		ui.clickLink("css=input[value='Copy']");
		
		log.info("INFO: Check that the 'Copy to Activity' text no longer displays");
		ui.fluentWaitTextNotPresent("Copy to Activity");
		
		log.info("INFO: Check that the entry appears");
		ui.fluentWaitTextPresent(entry.getTitle());

		log.info("INFO: Verify entry1 was copied and is a child of the activity");
		//Assert.assertTrue(driver.isElementPresent("css=div[class='nodeChildren'] > div:contains('" + entryName + "')"));
		List<Element> entries = driver.getElements("css=div[class='MiniNode'] h4 span:contains(" + entry.getTitle() + ")");
		Assert.assertTrue(entries.size()>1, "ERROR: The entry was not properly copied: " +
				"it does not appear more than once in the activity.");

		ui.endTest();
	}

	/**
	 * Attempt to create an activity and then delete it. Confirm the
	 * activity was moved to the trash folder. Attempt to restore the
	 * activity and confirm that the activity shows up in the regular
	 * activities list. Confirm that the recent updates show that the
	 * activity has been restored.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void deleteAndRestoreActivity() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();

		log.info("INFO: Create activity using API");
		activity.createAPI(apiOwner);

		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Click on the Activities tab");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);

		log.info("INFO: Find the UUID of the activity");
		String UUID = getActivityUUID(activity);

		log.info("INFO: Click the 'more' link for the activity");
		ui.clickLink("css=tr[uuid='" + UUID + "'] td.lotusLastCell a:contains('More')");

		log.info("INFO: Delete the activity");
		ui.clickLinkWait("css=tr[id$='" + UUID + "detailsRow'] a:contains('Delete')");
		ui.clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);

		log.info("INFO: Go to trash folder and verify deleted activity is present");
		ui.clickLink(ActivitiesUIConstants.Activities_LeftNav_Trash);
		Assert.assertTrue(driver.isTextPresent(activity.getName()),
				"Activity not visible in trash: " + activity.getName());

		log.info("INFO: Click the 'more' link for the activity");
		ui.clickLink("css=tr[uuid='" + UUID + "'] td.lotusLastCell a:contains('More')");

		log.info("INFO: Restore the activity");
		ui.clickLinkWait("css=tr[id$='" + UUID + "detailsRow'] a:contains('Restore')");

		log.info("INFO: Click on the Activities tab");
		ui.clickLink(ActivitiesUIConstants.Activities_Tab);

		log.info("INFO: Verify the activity has been restored");
		Assert.assertTrue(driver.isElementPresent("link=" + activity.getName()),
				activity.getName() + " activity link is not present");

		log.info("INFO: Click on the Recent Updates tab & verify recent updates lists the activity restore action");
		ui.clickLink(ActivitiesUIConstants.Recent_Updates_Tab);
		Assert.assertTrue(driver.isTextPresent("restored activity \"" + activity.getName() + "\""),
				activity.getName() + " activity link is not present");

		ui.endTest();
	}
	
	/**
	 * Create an activity. Attempt to follow it and verify that the option to
	 * follow changes to unfollow. Attempt to unfollow it and verify that the
	 * option to unfollow changes to follow.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void followActivity() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();
		
		log.info("INFO: Create activity using API");
		activity.createAPI(apiOwner);

		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Click on the Activity link");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: Click on the link 'Follow this Activity'");
		ui.clickLinkWait("link=Follow this Activity");
		Element stopFollowingLink = driver.getSingleElement("link=Stop Following this Activity");
		
		log.info("INFO: Verify the link 'Stop Following this Activity' appears");
		Assert.assertTrue(stopFollowingLink != null, 
				"ERROR: Attempted to follow an activity, but the " + "option to unfollow did not appear.");
		
		log.info("INFO: Click on the link 'Stop Following this Activity'");
		stopFollowingLink.click();
		
		log.info("INFO: Verify the link 'Follow this Activity' appears");
		Assert.assertTrue(driver.isElementPresent("link=Follow this Activity"),
				"ERROR: Attempted to unfollow an activity, but the " + "option to follow did not appear.");

		ui.endTest();
	}
	
	/**
	 * Attempt to create an activity. Attempt to create an entry with multiple
	 * bookmarks with forbidden URL schemes. Verify that none of the bookmarks
	 * use the forbidden URL scheme, and instead open a blank page.
	 * @throws Exception
	 */	
	@Test(groups = {"regression", "regressioncloud"} )
	public void invalidBookmarks() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();

		log.info("INFO: Create activity using API");
		activity.createAPI(apiOwner);

		//Generate Entry Name
		String entryRandomNumber = Helper.genDateBasedRandVal();
		String entryName = Data.getData().Start_A_Entry_InputText_Title_Data + entryRandomNumber;

		//Bookmark Names/URLs
		String bookmarkNameJS = "bookmarkJS" + entryRandomNumber;
		String bookmarkNameNews = "bookmarkNews" + entryRandomNumber;
		String bookmarkNameData = "bookmarkData" + entryRandomNumber;
		String bookmarkNameFile = "bookmarkFile" + entryRandomNumber;

		String bookmarkURLJS = "javascript:alert('Oh hi there!')";
		String bookmarkURLNews = "news:alt.flame";
		String bookmarkURLData = "data:0xDEADBEEF";
		String bookmarkURLFile = "file:/whatever.txt";

		String bookmarkName[] = {bookmarkNameJS, bookmarkNameNews, bookmarkNameData, bookmarkNameFile};
		String bookmarkURL[] = {bookmarkURLJS, bookmarkURLNews, bookmarkURLData, bookmarkURLFile};

		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Click on the activity link");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
				
		BaseActivityEntry entry = BaseActivityEntry.builder(entryName)
				                                   .tags(Helper.genDateBasedRandVal())
				                                   .bookmark(bookmarkName[0], bookmarkURL[0])
				                                   .bookmark(bookmarkName[1], bookmarkURL[1])
				                                   .bookmark(bookmarkName[2], bookmarkURL[2])
				                                   .bookmark(bookmarkName[3], bookmarkURL[3])
				                                   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				                                   .build();

		log.info("INFO: Create Entry");
		entry.create(ui);

		log.info("INFO: Verify all bookmarks open blank pages since bookmarks are invalid");
		for(int i = 0; i < bookmarkName.length; i++){
			log.info("INFO: verifying invalid bookmark " + bookmarkURL[i] + " is handled properly");
			Element linkElement = ui.getFirstVisibleElement("link=" + bookmarkName[i]);
			Assert.assertNotNull(linkElement, "The bookmark " + bookmarkName[i] + 
					" did not appear in the activity entry after being added!");
			String linkHref = linkElement.getAttribute("href");
			Assert.assertTrue(linkHref.equals("about:blank"),
					"The invalid bookmark " + bookmarkURL[i] +
					" was not properly replaced by a blank page.");
		}
		ui.endTest();
	}

	/**
	 * Create an activity. Attempt to change the activity's access to public.
	 * Verify the activity shows up in the list of public activities for the
	 * user that created it. Log out and log in as another user, and verify
	 * that the activity can be seen by that user.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void makeActivityPublic() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();

		log.info("INFO: Create activity using API");
		activity.createAPI(apiOwner);

		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Click on the activity link");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: Click on the Members link");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Members);
		
		log.info("INFO: Click on the Change link");
		ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.Change_Access);
		ui.getFirstVisibleElement(ActivitiesUIConstants.Change_Access).click();
		
		log.info("INFO: Select the Public access radio button");
		ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.PublicAccess_RadioBtn);
		ui.getFirstVisibleElement(ActivitiesUIConstants.PublicAccess_RadioBtn).click();

		log.info("INFO: Save the Activity change to Public access");	
		ui.clickSaveButton();

		if(isOnPremise){
			log.info("INFO: Verify that the access for the activity has changed to 'Public' in the UI");
			Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.Activity_Public_Access_On_Prem),
					"ERROR: The access for the activity did NOT change to 'Public' in the UI as expected");

		}else{
			log.info("INFO: Verify that the access for the activity has changed to 'Public' in the UI");
			Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.Activity_Public_Access_SC),
					"ERROR: The access for the activity did NOT change to 'Public' in the UI as expected");

		};	


		log.info("INFO: Verify the activity appears under My Activities and Public Activities views");
		ui.clickLink(ActivitiesUIConstants.Activities_Tab);
		ui.fluentWaitPresent(ActivitiesUI.getActivityLink(activity));
		ui.getFirstVisibleElement(ActivitiesUIConstants.PublicActivities_Active).click();
		ui.fluentWaitPresent(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: return to My Activities view");
		ui.clickLink(ActivitiesUIConstants.Activities_Tab);
		ui.fluentWaitPresent(ActivitiesUI.getActivityLink(activity));
		
		log.info("INFO: Logout of Activities as: " + testUser.getDisplayName());
		ui.logout();
		ui.close(cfg);

		log.info("INFO: Log into Activities as a different user: " + testLookAheadUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testLookAheadUser);

		log.info("INFO: Verify the activity appears under Public Activities");
		ui.getFirstVisibleElement(ActivitiesUIConstants.PublicActivities_Active).click();
		ui.fluentWaitPresent(ActivitiesUI.getActivityLink(activity));

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a complete mention with your message</li>
	*<li><B>Step: </B>In CKE box, type complete mention and attempt to click</li>
	*<li><B>Verify: </B>Ensure no new windows/tabs are created</li>
	*<li><B>Verify: </B>The message was displayed exactly as typed</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionPreventClick() {

		String testName = ui.startTest();
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

		log.info("INFO: Create activity using API");		
		activity.createAPI(apiOwner);
		
		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Open activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: Type mention in description field (CKEditor)");
		ui.clickLink(ActivitiesUIConstants.New_Entry);
		ui.selectAtMentionCKE(testUser.getDisplayName());
		driver.switchToFrame().selectSingleFrameBySelector(ActivitiesUIConstants.New_Entry_Description);
				
		log.info("INFO: Click on the complete mention link");
		ui.clickLink(ui.getMentionsLink(testUser));
		
		log.info("INFO: Check if link opened new window");
		boolean window = driver.getWindowHandles().equals(testUser + " Profile");
		
		log.info("INFO: Verify displayed message matches intended message");
		boolean actual = window;
		boolean expected = false;
		Assert.assertEquals(actual, expected);

		ui.endTest();	
	}
	
	/**
	 * Verify that the "You have a form that has not been saved. Discard your
	 * changes?" prompt appears when navigating away from the following:
	 * <ul><li>Creating an activity
	 * <li>Creating an entry for an activity
	 * <li>Creating a to-do for an activity
	 * <li>Creating a section for an activity
	 * <li>Creating an activity template</ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void verifyCompleteFormPrompts() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();

		//Generate Entry Name
		String entryName = testName + " entry" + Helper.genDateBasedRandVal();

		log.info("INFO: Log into Activities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Click on the link to start an activity");
		ui.clickLinkWait(ActivitiesUIConstants.Start_An_Activity);

		log.info("INFO: Fill in the activity name field on the form");
		ui.typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Name, activity.getName());

		log.info("INFO: Navigate away from the page - click To Do List tab");
		ui.clickLinkWait(ActivitiesUIConstants.ToDoListTab);

		log.info("INFO: Verify complete form prompt appears");
		verifyCompleteFormPrompt();

		log.info("INFO: Click the Cancel button so activity is not created");
		ui.clickCancelButton();

		log.info("INFO: Create an activity");
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity);
		ui.create(activity);

		log.info("INFO: Click on the link to create an entry & enter something into the Title field");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);
		ui.fluentWaitElementVisible(ActivitiesUIConstants.New_Entry_InputText_Title);
		ui.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entryName);

		log.info("INFO: Navigate away from the add entry page - click To Do List tab");
		ui.clickLinkWait(ActivitiesUIConstants.ToDoListTab);

		log.info("INFO: Verify complete form prompt appears");
		verifyCompleteFormPrompt();

		log.info("INFO: Click on the Cancel button so the entry is not created");
		ui.clickCancelButton();

		log.info("INFO: Click on the button to add a todo & enter something into the Title field");
		ui.clickLink(ActivitiesUIConstants.AddToDo);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, Data.getData().BVT_Level3_ToDo_InputText_Title_Data);

		log.info("INFO: Navigate away from the add todo page - click To Do List tab");
		ui.clickLinkWait(ActivitiesUIConstants.ToDoListTab);

		log.info("INFO: Verify complete form prompt appears");
		verifyCompleteFormPrompt();

		log.info("INFO: Click on the Cancel button so the todo is not created");
		ui.clickCancelButton();

		log.info("INFO: Click on the link to add a section & enter something into the input field");
		ui.clickLinkWait(ActivitiesUIConstants.AddSection);
		ui.typeText(ActivitiesUIConstants.Section_InputText_Title, "New section for " + testName);

		log.info("INFO: Navigate away from the section page - click To Do List tab");
		ui.clickLinkWait(ActivitiesUIConstants.ToDoListTab);

		log.info("INFO: Verify complete form prompt appears");
		verifyCompleteFormPrompt();

		log.info("INFO: Click on the Cancel button so the Section is not created");
		ui.clickCancelButton();

		log.info("INFO: Click on the Activity Templates tab & then the Create a Template button");
		ui.clickLinkWait(ActivitiesUIConstants.TemplateTab);
		ui.clickLinkWait(ActivitiesUIConstants.createATemplate);

		log.info("INFO: Enter some text into the Name field");
		ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.Template_InputText_Title);
		ui.typeText(ActivitiesUIConstants.Template_InputText_Title, "New section for " + testName);

		log.info("INFO: Navigate away from the create a template form - click To Do List tab");
		ui.clickLinkWait(ActivitiesUIConstants.ToDoListTab);

		log.info("INFO: Verify complete form prompt appears");
		verifyCompleteFormPrompt();

		ui.endTest();
	}
	
	/**
	 * Attempt to create an activity. Attempt to create an entry in the 
	 * activity with a custom text field. Verify the entry was created with
	 * the custom field. Attempt to edit the entry's custom field and
	 * verify that the entry's custom field reflects the edit.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void editCustomEntryLabels() throws Exception {
		String testName = ui.startTest();
		String customTextLabel = "Level 3 Text";
		String customTextEdited = "Level 3.1 Text";
		String customTextContent = "Text content for " + testName + Helper.genDateBasedRand();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		                                        .tags(testName)
		                                        .goal("Goal for "+ testName)
		                                        .build();

		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRand())
				                                   .tags(Helper.genDateBasedRandVal())
				                                   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				                                   .customText(customTextLabel, customTextContent)
				                                   .build();
		
		log.info("INFO: Create activities using API");
		activity.createAPI(apiOwner);

		log.info("INFO: Log into Activities as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Click on the activity link");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));		

		log.info("INFO: Create an activity entry");
		ui.createEntry(entry);
		
		log.info("INFO: Verify entry content and title");
		ui.fluentWaitTextPresent(customTextContent);
		Assert.assertTrue(driver.isTextPresent(customTextLabel),
				"ERROR: The custom label \"" + customTextLabel + 
				"\" did not appear in the entry " + entry.getTitle());
		
		log.info("INFO: Click the Edit link");
		ui.clickLink(ActivitiesUIConstants.editEntry);
				
		log.info("INFO: Edit the custom label");
		String labelSelector = "css=span[id^=lconn_act_TextField_][id$=-fieldLabel]:contains('" + customTextLabel + "')";
		ui.clickLinkWait(labelSelector);
		String inputSelector = "css=input[id^='dijit_form_TextBox_']";
		ui.clearText(inputSelector);
		ui.typeText(inputSelector, customTextEdited);
		
		log.info("INFO: Click away from input to commit label");
		driver.getSingleElement(ActivitiesUIConstants.New_Entry_AddCustomFieldsMenu).click();
		ui.clickSaveButton();
		
		log.info("INFO: Verify entry content and title");
		ui.fluentWaitTextPresent(customTextContent);
		Assert.assertTrue(driver.isTextPresent(customTextEdited),
				"ERROR: The custom label \"" + customTextEdited +
				"\" did not appear in the entry " + entry.getTitle());
		
		ui.endTest();
	}	
	
	/**
	 * Attempts to create five different activities with names containing
	 * special characters and confirms they were created.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} )
	public void verifyActivityNames() throws Exception {
		String testName = ui.startTest();

		//Generate Activity Names
		String activityRandomNumber = Helper.genDateBasedRandVal();
		String[] activityNames = new String[5];

		activityNames[0] = Data.getData().Start_SpecialChar + activityRandomNumber;
		activityNames[1] = Data.getData().Contains_SpecialChar + activityRandomNumber;
		activityNames[2] = Data.getData().Ends_SpecialChar + activityRandomNumber;
		activityNames[3] = Data.getData().Single_SpecialChar + activityRandomNumber;
		activityNames[4] = Data.getData().All_SpecialChars + activityRandomNumber;

		log.info("INFO: Log into Activities as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Create/verify new activities with special names");
		for(int i = 0; i < activityNames.length; i++) {
			BaseActivity activity = new BaseActivity.Builder(activityNames[i])
			                                        .tags(testName)
			                                        .goal("Goal for "+ testName)
			                                        .build();

			log.info("INFO: Try to create new activity with a name that contains special chars"); 
			ui.create(activity);
			Assert.assertTrue(ui.fluentWaitTextPresent(activityNames[i]), activityNames[i] + " activity link is not present");
			ui.clickLink(ActivitiesUIConstants.Activities_Tab);
		}

		ui.endTest();
	}	
	
	/**
	 * Attempts to return the UUID for the given activity. Assumes the client
	 * is currently on the main activities tab, and that the activity is
	 * listed in this tab.
	 * @param activity
	 * 		The activity to find the UUID of
	 * @return
	 * 		The UUID of the activity, if found
	 * @throws
	 * 		AssertionError, if the activity's UUID cannot be found
	 */
	private String getActivityUUID (BaseActivity activity) {
		//Find the link to our activity
		Element activityLink = driver.getSingleElement("link=" + activity.getName());
		Assert.assertFalse(activityLink == null, "No activity with found with name " + activity.getName());

		//extract the Activity UUID from the link with a regular expression 
		String activityLinkURL = activityLink.getAttribute("href");
		Pattern uidPattern = Pattern.compile(",([0-9a-f-]+)$");
		Matcher uidMatcher = uidPattern.matcher(activityLinkURL);
		Assert.assertTrue(uidMatcher.find(), "No valid UUID found for activity "
				+ activity.getName()
				+ ", link was " + activityLinkURL);
		return uidMatcher.group(1); 
	}
	
	private void verifyCompleteFormPrompt() {
		ui.fluentWaitTextPresent(Data.getData().PleaseCompleteForm);
		Assert.assertTrue(driver.isTextPresent(Data.getData().PleaseCompleteForm));
		Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.navigateAwayPromptOK));
		Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.navigateAwayPromptCancel));
		ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.navigateAwayPromptClose);
		List<Element> closeButtons = driver.getVisibleElements(ActivitiesUIConstants.navigateAwayPromptClose);
		Assert.assertTrue(closeButtons.size() == 1, "Wrong number of elements visible for: {" +
				ActivitiesUIConstants.navigateAwayPromptClose +"}. " +
				closeButtons.size() + " elements found, 1 element expected.");
		closeButtons.get(0).click();
	}

}
