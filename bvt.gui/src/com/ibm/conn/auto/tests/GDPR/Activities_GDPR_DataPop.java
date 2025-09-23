package com.ibm.conn.auto.tests.GDPR;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityTemplate;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Activities_GDPR_DataPop extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Activities_GDPR_DataPop.class);
	private TestConfigCustom cfg; ICBaseUI ui;
	private CommunitiesUI commUI;
	private ActivitiesUI activitiesUI;
	private APICommunitiesHandler apiCommOwner1,apiCommOwner2;
	private APIActivitiesHandler apiActOwner1, apiActOwner2;
	private String serverURL;
	private User testUser1, testUser2;


	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		activitiesUI = ActivitiesUI.getGui(cfg.getProductName(), driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		URLConstants.setServerURL(serverURL);
				
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiActOwner1 = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		apiActOwner2 = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Activity with an: Entry,ToDo,Section & Comment</li>
	 *<li><B>Step:</B> UserA creates a stand alone activity</li>
	 *<li><B>Step:</B> UserA adds an entry with a file attachment</li>
	 *<li><B>Step:</B> UserA edits the entry & adds a tag</li>
	 *<li><B>Step:</B> UserA creates a nested Todo under the entry</li>
	 *<li><B>Step:</B> UserA creates a Section</li>
	 *<li><B>Step:</B> UserA logs out and UserB logs into Activities</li>
	 *<li><B>Step:</B> UserB opens the activity created by UserA</li>
	 *<li><B>Step:</B> UserB adds a comment to the entry</li>
	 *</ul>
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void activityWithEntryTodoSectionComment() {

		BaseActivity activity = new BaseActivity.Builder("activityWithEntryTodoSectionComment " + Helper.genDateBasedRand())
		                                        .goal("GDPR data population - UserB will add a comment to the entry " + Helper.genDateBasedRandVal())
		                                        .addMember(new ActivityMember(ActivityRole.AUTHOR, testUser2, ActivityMember.MemberType.PERSON))
		                                        .tags(Data.getData().TagForMyBookmarks)	                                   
		                                        .build();	

		BaseActivityEntry entry = BaseActivityEntry.builder("Test Entry " + Helper.genDateBasedRandVal())
				                                   .description("GDPR data pop - standalone activity entry " + Helper.genDateBasedRandVal())
				                                   .addFile(Data.getData().file1)
				                                   .build();

		BaseActivityToDo newToDo = BaseActivityToDo.builder("Nested ToDo " + Helper.genDateBasedRandVal())
				                                   .description("GDPR data pop - ToDo under/below the entry " + Helper.genDateBasedRandVal())
				                                   .tags(Data.getData().MultiFeedsTag1)
				                                   .build();

		String sectionTitle = Data.getData().Start_An_Activity_Section_Title + Helper.genDateBasedRandVal();

		activitiesUI.startTest();

		log.info("INFO: Log into Activities as: " + testUser1.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser1);

		log.info("INFO: Creating a new Activity: " + activity.getName());
		activity.create(activitiesUI);		

		log.info("INFO: UserA, " + testUser1.getDisplayName() + " , creates an activity entry");
		entry.create(activitiesUI);	

		log.info("INFO: Get the activity entry UUID - to be used later");
		String entryUUID = activitiesUI.getEntryUUID(entry);  

		log.info("INFO: Open the entry");
		activitiesUI.expandEntry(entryUUID);

		log.info("INFO: Click the entry Edit link");
		activitiesUI.clickLink(ActivitiesUIConstants.editEntry);

		log.info("INFO: Add a tag to the entry");
		driver.getFirstElement(ActivitiesUIConstants.New_Entry_InputText_Tags).type(Data.getData().TagForWikiPages);

		log.info("INFO: Save the tag to the entry - click Save button");
		activitiesUI.clickSaveButton();

		log.info("INFO: Create a ToDo (nested) below the Entry");
		activitiesUI.createTodoUnderEntry(entryUUID,newToDo);

		log.info("INFO: Create a Section");
		activitiesUI.addSection(sectionTitle);

		log.info("INFO: Log out as UserA: " + testUser1.getDisplayName());
		activitiesUI.logout();
		activitiesUI.close(cfg);

		log.info("INFO: Log into Activities as: " + testUser2.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser2);

		log.info("INFO: Open the activity");
		activitiesUI.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: UserB, " + testUser2.getDisplayName() + " adds a comment to the entry created by UserA: " + testUser1.getDisplayName());
		activitiesUI.createCommentUnderEntry(entryUUID,Data.getData().Contains_SpecialChar);

		activitiesUI.endTest();

	}	

	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Activity with a ToDo</li>
	 *<li><B>Step:</B> UserA creates a stand alone activity</li>
	 *<li><B>Step:</B> UserA add a todo with a file attachment</li>
	 *<li><B>Step:</B> UserA edits the todo & adds a tag</li>
	 *<li><B>Step:</B> UserB adds a comment to the todo</li>
	 *</ul>
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void activityWithTodoAndComment() {

		BaseActivity activity = new BaseActivity.Builder("activityWithToDoAndComment " + Helper.genDateBasedRand())
		                                        .goal("GDPR data population - UserB adds a comment to the todo " + Helper.genDateBasedRandVal())
		                                        .addMember(new ActivityMember(ActivityRole.AUTHOR, testUser2, ActivityMember.MemberType.PERSON))
		                                        .tags(Data.getData().TagForMyBookmarks)	                                   
		                                        .build();	

		BaseActivityToDo todo = BaseActivityToDo.builder("ToDo " + Helper.genDateBasedRandVal())
				                                .description("GDPR data pop - ToDo with a file attachment " + Helper.genDateBasedRandVal())
				                                .addFile(Data.getData().file2)
				                                .build();

		activitiesUI.startTest();

		log.info("INFO: Log into Activities as UserA: " + testUser1.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser1);

		log.info("INFO: Create a new Activity: " + activity.getName());
		activity.create(activitiesUI);

		log.info("INFO: UserA, " + testUser1.getDisplayName() + " , creates a ToDo");
		todo.create(activitiesUI);	

		log.info("INFO: Get the activity ToDo UUID - to be used later");
		String todoUUID = activitiesUI.getEntryUUID(todo);  

		log.info("INFO: Open the todo");
		activitiesUI.expandEntry(todoUUID);

		log.info("INFO: Click the todo Edit link");
		activitiesUI.clickLink(ActivitiesUIConstants.editEntry);

		log.info("INFO: Click on the To Do More Options link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Add a tag to the todo");
		driver.getFirstElement(ActivitiesUIConstants.ToDo_InputText_Tags).type(Data.getData().TagForWikiPages);

		log.info("INFO: Save the tag to the entry - click Save button");
		activitiesUI.clickSaveButton();

		log.info("INFO: Log out as UserA: " + testUser1.getDisplayName());
		activitiesUI.logout();
		activitiesUI.close(cfg);

		log.info("INFO: Log into Activities as UserB: " + testUser2.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser2);

		log.info("INFO: Open the activity");
		activitiesUI.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: UserB: " + testUser2.getDisplayName() + " adds a comment to the ToDo created by UserA: " + testUser1.getDisplayName());
		activitiesUI.createCommentUnderEntry(todoUUID,Data.getData().Contains_SpecialChar);

		activitiesUI.endTest();	
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Activity with an Entry, ToDo & Comment</li>
	 *<li><B>Step:</B> UserB creates a stand-alone activity</li>
	 *<li><B>Step:</B> UserB adds UserA to the activity as an Owner</li>
	 *<li><B>Step:</B> UserB adds an Entry</li>
	 *<li><B>Step:</B> UserB add a ToDo and assigns it to UserA</li>
	 *<li><B>Step:</B> UserA adds a comment to the Entry</li>
	 *<li><B>Step:</B> UserA edits the Entry</li>
	 *</ul>
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void activityWithEntryTodoAndComment() {

		BaseActivity activity = new BaseActivity.Builder("activityWithEntryToDoAndComment " + Helper.genDateBasedRand())
		                                        .goal("GDPR data population - UserA edits the entry & adds a comment to the entry " + Helper.genDateBasedRandVal())
		                                        .addMember(new ActivityMember(ActivityRole.OWNER, testUser1, ActivityMember.MemberType.PERSON))
		                                        .tags(Data.getData().TagForMyBookmarks)	                                   
		                                        .build();	

		BaseActivityToDo todo = BaseActivityToDo.builder("ToDo " + Helper.genDateBasedRandVal())
				                                .description("GDPR data pop - ToDo is assigned to activity member UserA " + Helper.genDateBasedRandVal())
				                                .build();
		
		BaseActivityEntry entry = BaseActivityEntry.builder("Test entry " + Helper.genDateBasedRandVal())
                                                   .description("GDPR data pop - activity member, UserA, edits this entry " + Helper.genDateBasedRandVal())
                                                   .build();

		activitiesUI.startTest();

		log.info("INFO: Log into Activities as UserB: " + testUser2.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser2);

		log.info("INFO: Create a new Activity: " + activity.getName());
		activity.create(activitiesUI);
		
		log.info("INFO: UserB: " + testUser2.getDisplayName() + " , creates an activity Entry");
		entry.create(activitiesUI);	
		
		log.info("INFO: Get the activity entry UUID - to be used later");
		String entryUUID = activitiesUI.getEntryUUID(entry);

		log.info("INFO: UserB: " + testUser2.getDisplayName() + " , creates an activity ToDo");
		todo.create(activitiesUI);	
		
		log.info("INFO: Get the activity ToDo UUID ");
		String todoUUID = activitiesUI.getEntryUUID(todo);
		
		log.info("INFO: Assign the ToDo to UserA " + testUser1.getDisplayName());
		activitiesUI.addNewAssignee(todoUUID, testUser1.getDisplayName());
		
		log.info("INFO: Log out of Activities as UserB " + testUser2.getDisplayName());
		activitiesUI.logout();
		activitiesUI.close(cfg);
		
		log.info("INFO: Log into Activities as UserA " + testUser1.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser1);	
		
		log.info("INFO: Open the activity");
		activitiesUI.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		log.info("INFO: Open the Entry");
		activitiesUI.expandEntry(entryUUID);

		log.info("INFO: Click the Entry Edit link");
		activitiesUI.clickLink(ActivitiesUIConstants.editEntry);
		
		log.info("INFO: Edit the Entry description");
		driver.getFirstElement(ActivitiesUIConstants.New_Entry_InputText_Title).type(Data.getData().BlogsNewEntryTitle);
		
		log.info("INFO: Save the change made to the entry - click Save button");
		activitiesUI.clickSaveButton();
		
		log.info("INFO: UserA: " + testUser1.getDisplayName() + ", adds a comment to the Entry created by UserB ," + testUser2.getDisplayName());
		activitiesUI.createCommentUnderEntry(entryUUID,Data.getData().Contains_SpecialChar);

		activitiesUI.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Delete Stand-alone Activity</li>
	 *<li><B>Step:</B> UserA creates a stand alone activity</li>
	 *<li><B>Step:</B> UserA deletes the activity</li>
	 *</ul>
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void deleteStandaloneActivity() {
		
		BaseActivity activity = new BaseActivity.Builder("deleteStandaloneActivity " + Helper.genDateBasedRand())
		                                        .goal("GDPR data population - put activity into the trash " + Helper.genDateBasedRandVal())
		                                        .tags(Data.getData().TagForMyBookmarks)	                                   
		                                        .build();	


		activitiesUI.startTest();

		log.info("INFO: Log into Activities as UserA: " + testUser1.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser1);

		log.info("INFO: Create a new Activity: " + activity.getName());
		activity.create(activitiesUI);
		
		log.info("INFO: Click on Activity Actions link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.activityActionMenu);
			
		log.info("INFO: From the Activity Actions menu select Delete Activity");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.actionMenuDeleteActivity);
		
		log.info("INFO: Click OK on the delete activity confirmation dialog");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);
		
		activitiesUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Create Activity Template</li>
	 *<li><B>Step:</B> UserA creates an Activity Template</li>
	 *</ul>
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void createActivityTemplate() {
		
		BaseActivityTemplate template = new BaseActivityTemplate.Builder("createActivityTemplate " + Helper.genDateBasedRand())
		                                                        .tags(Data.getData().Start_An_Activity_Template_InputText_Tags_Data + Helper.genDateBasedRand())
		                                                        .description("GDPR data population - Activity Template description" + Helper.genDateBasedRand())
		                                                        .build();


		activitiesUI.startTest();

		log.info("INFO: Log into Activities as UserA: " + testUser1.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser1);

		log.info("INFO: Create a new Activity: " + template.getName());
		template.create(activitiesUI);
		
		activitiesUI.endTest();

	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Edit Activity Template</li>
	 *<li><B>Step:</B> UserB creates an Activity Template with UserA as an Owner</li>
	 *<li><B>Step:</B> UserA edits the Activity Template</li>
	 *</ul>
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void additionalOwnerEditsActivityTemplate() {
		
		BaseActivityTemplate activityTemplate = new BaseActivityTemplate.Builder("additionalOwnerEditsActivityTemplate " + Helper.genDateBasedRand())
		                                                                .tags(Data.getData().Start_An_Activity_Template_InputText_Tags_Data + Helper.genDateBasedRand())
		                                                                .description("GDPR data population - Activity Template edited by activity member (UserA)" + Helper.genDateBasedRand())
		                                                                .addMember(new ActivityMember(ActivityRole.OWNER, testUser1, ActivityMember.MemberType.PERSON))
		                                                                .build();
		
		String activityTemplateTitle = "link=" + activityTemplate.getName();

		activitiesUI.startTest();

		log.info("INFO: Log into Activities as UserB: " + testUser2.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser2);

		log.info("INFO: Create an activity template: " + activityTemplate.getName());
		activityTemplate.create(activitiesUI);

		log.info("INFO: Log out as UserB " + testUser2.getDisplayName());
		activitiesUI.logout();
		activitiesUI.close(cfg);

		log.info("INFO: Log into Activities as UserA: " + testUser1.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser1);

		log.info("INFO: Click on the Activity Template tab");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.TemplateTab);

		log.info("INFO: Open the activity template");
		activitiesUI.clickLinkWait(activityTemplateTitle);

		log.info("INFO: Click on the Template Actions link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.templateActionsLink);

		log.info("INFO: Click on the Edit Template menu option");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.editTemplateOption);

		log.info("INFO: Clear the existing content from the Tags field");
		driver.getFirstElement(ActivitiesUIConstants.Template_InputText_Tags).clear();

		log.info("INFO: Add new tags to the template Tags field");
		driver.getFirstElement(ActivitiesUIConstants.Template_InputText_Tags).type(Data.getData().MultiFeedsTag1);

		log.info("INFO: Save the tags to the template - click the Save button");
		activitiesUI.clickSaveButton();

		activitiesUI.endTest();

	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Follow Stand-Alone Activity</li>
	 *<li><B>Step:</B> UserB creates an activity</li>
	 *<li><B>Step:</B> UserA follows the activity</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void followStandaloneActivity() {
		
		BaseActivity activity = new BaseActivity.Builder("followStandAloneActivity " + Helper.genDateBasedRand())
		                                        .goal("GDPR data population - activity member (UserA) follows the stand-alone activity created by UserB" )
		                                        .addMember(new ActivityMember(ActivityRole.AUTHOR, testUser1, ActivityMember.MemberType.PERSON))
		                                        .build();
		
		activitiesUI.startTest();
		
		log.info("INFO: log into Activities as UserB " + testUser2.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser2);

		log.info("INFO: Create an Activity");
		activity.create(activitiesUI);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		activitiesUI.logout();
		activitiesUI.close(cfg);
		
		log.info("INFO: log into Activities as UserA: " + testUser1.getDisplayName());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.login(testUser1);
		
		log.info("INFO: Open the activity");
		activitiesUI.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		log.info("INFO: UserA: " + testUser1.getDisplayName() + " will Follow the Activity");		
		activitiesUI.clickLinkWait(ActivitiesUIConstants.followThisActivityLink);
		
		log.info("Logout as UserA: " + testUser1.getDisplayName());
		activitiesUI.logout();
		activitiesUI.close(cfg);
				
		activitiesUI.endTest();
	}
	
		
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Activity with Entry, Section & Comment</li>
	 *<li><B>Step:</B> UserA creates a community & adds UserB as an Owner</li>
	 *<li><B>Step:</B> UserA creates a community activity</li>
	 *<li><B>Step:</B> UserA adds an entry with a file attachment community activity</li>
	 *<li><B>Step:</B> UserA edits the entry & adds a Tag</li>
	 *<li><B>Step:</B> UserA adds a section to the community activity</li>
	 *<li><B>Step:</B> UserB adds a comment to the entry created by UserA</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"}, enabled=false)
	public void commActivityWithEntrySectionAndComment() {
				
		BaseCommunity community = new BaseCommunity.Builder("commActivityWithEntrySectionAndComment " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .addMember(new Member(CommunityRole.OWNERS, testUser2))
                                      .description("GDPR data population - Public community with a community activity")
                                      .build();
	
		BaseActivity commActivity = new BaseActivity.Builder("community activity 1" + Helper.genDateBasedRand())
												.goal("GDPR data pop - community activity with an entry, section and comment to entry ")
												.community(community)
												.build();
		
		BaseActivityEntry entry1 = BaseActivityEntry.builder("commActivityEntry" + Helper.genDateBasedRandVal())
				                                    .description("GDPR data pop - test entry " + Helper.genDateBasedRandVal()) 
				                                    .addFile(Data.getData().file3)
				                                    .build();
		
		String sectionTitle = Data.getData().Start_An_Activity_Section_Title + Helper.genDateBasedRandVal();
			
		String commActivityTitle = "link=" + commActivity.getName();
		
		commUI.startTest();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner1);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI1);

		log.info("INFO: Add the 'Activities' widget to the Community using API");
		community.addWidgetAPI(comAPI1, apiCommOwner1, BaseWidget.ACTIVITIES);

		log.info("INFO: Add a community activity using the API");
		commActivity.createAPI(apiActOwner1, community);
		
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Open the Community");
		community.navViaUUID(commUI);
				
		log.info("INFO: Navigate to the community activity");
		commUI.clickLinkWithJavascript(commActivityTitle);
		
		log.info("INFO: Add an Entry to the community activity");		
		activitiesUI.createEntry(entry1);
		
		log.info("INFO: Get the Entry UUID");
		String entryUUID = activitiesUI.getEntryUUID(entry1);
		
		log.info("INFO: Open the Entry");
		activitiesUI.expandEntry(entryUUID);

		log.info("INFO: Click the Entry Edit link");
		activitiesUI.clickLink(ActivitiesUIConstants.editEntry);

		log.info("INFO: Add a tag to the Entry");
		driver.getFirstElement(ActivitiesUIConstants.New_Entry_InputText_Tags).type(Data.getData().TagForWikiPages);

		log.info("INFO: Save the tag to the Entry - click Save button");
		activitiesUI.clickSaveButton();
	
		log.info("INFO: Add a Section to the community activity");
		activitiesUI.addSection(sectionTitle);
		
		log.info("INFO: Log out as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log into Communities as UserB " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		log.info("INFO: Open the Community");
		community.navViaUUID(commUI);
				
		log.info("INFO: Navigate to the community activity");
		commUI.clickLinkWait(commActivityTitle);
		
		log.info("INFO: UserB: " + testUser2.getDisplayName() + " adds a comment to the Entry");
		activitiesUI.createCommentUnderEntry(entryUUID,Data.getData().Contains_SpecialChar);

		commUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Activity with ToDo, Comment & a nested ToDo</li>
	 *<li><B>Step:</B> UserA creates a community & adds UserB as an Owner</li>
	 *<li><B>Step:</B> UserA adds the Activities widget</li>
	 *<li><B>Step:</B> UserA creates a community activity</li>
	 *<li><B>Step:</B> UserA adds a ToDo with a file attachment to the comm activity</li>
	 *<li><B>Step:</B> UserA edits the ToDo & adds a Tag</li>
	 *<li><B>Step:</B> UserA adds a nested ToDo to the existing top-level ToDo</li>
	 *<li><B>Step:</B> UserA adds a comment to the top-level ToDo</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"}, enabled=false)
	public void commActivityWithTodosAndComment(){
		
		BaseCommunity community = new BaseCommunity.Builder("commActivityWithTodosAndComment " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .addMember(new Member(CommunityRole.OWNERS, testUser2))
                                      .description("GDPR data population - Public community with a community activity")
                                      .build();
	
		BaseActivity commActivity = new BaseActivity.Builder("community activity 1" + Helper.genDateBasedRand())
												.goal("GDPR data pop - community activity with a top-level ToDo, nested ToDo and comment to the top-level ToDo ")
												.community(community)
												.build();
		
		BaseActivityToDo todo1 = BaseActivityToDo.builder("Top-Level ToDo " + Helper.genDateBasedRandVal())
                                                .description("GDPR data pop - Top-level ToDo " + Helper.genDateBasedRandVal())
                                                .addFile(Data.getData().file4)
                                                .build();
		
		BaseActivityToDo todo2 = BaseActivityToDo.builder("ToDo2 nested " + Helper.genDateBasedRandVal())
                                                 .description("GDPR data pop - Nested ToDo " + Helper.genDateBasedRandVal())
                                                 .build();
			
		String commActivityTitle = "link=" + commActivity.getName();
		
		commUI.startTest();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner1);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI1);

		log.info("INFO: Add the 'Activities' widget to the Community using API");
		community.addWidgetAPI(comAPI1, apiCommOwner1, BaseWidget.ACTIVITIES);

		log.info("INFO: Add a community activity using the API");
		commActivity.createAPI(apiActOwner1, community);
		
		log.info("INFO: Log into Communities as UserA " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Open the Community");
		community.navViaUUID(commUI);
				
		log.info("INFO: Navigate to the community activity");
		commUI.clickLinkWait(commActivityTitle);
		
		log.info("INFO: Add a ToDo to the community activity");		
		todo1.create(activitiesUI);
		
		log.info("INFO: Get the activity ToDo UUID");
		String todo1UUID = activitiesUI.getEntryUUID(todo1);  

		log.info("INFO: Open the ToDo");
		activitiesUI.expandEntry(todo1UUID);

		log.info("INFO: Click the ToDo Edit link");
		activitiesUI.clickLink(ActivitiesUIConstants.editEntry);

		log.info("INFO: Click on the ToDo More Options link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Add a tag to the ToDo");
		driver.getFirstElement(ActivitiesUIConstants.ToDo_InputText_Tags).type(Data.getData().TagForWikiPages);

		log.info("INFO: Save the tag to the ToDo - click Save button");
		activitiesUI.clickSaveButton();
	
		log.info("INFO: Create a ToDo (nested) below the top-level ToDo");
		activitiesUI.createTodoUnderEntry(todo1UUID,todo2);
		
		log.info("INFO: Add a comment to the top-level ToDo");
		activitiesUI.createCommentUnderEntry(todo1UUID,Data.getData().Contains_SpecialChar);
			
		commUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Activity with Entry, ToDo & Comment</li>
	 *<li><B>Step:</B> UserB creates a community & adds UserA as an Owner</li>
	 *<li><B>Step:</B> UserB adds the Activity app & creates a community activity</li>
	 *<li><B>Step:</B> UserB adds a ToDo & assigns it to UserA</li>
	 *<li><B>Step:</B> UserA adds an entry to the community activity</li>
	 *<li><B>Step:</B> UserA adds a comment to the ToDo created by UserB</li>
	 *<li><B>Step:</B> UserA edits the ToDo</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void commActivityEntryTodoAndComment(){
		
		BaseCommunity community = new BaseCommunity.Builder("commActivityWithEntryTodoAndComment " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .addMember(new Member(CommunityRole.OWNERS, testUser1))
                                      .description("GDPR data population - Public community with a community activity")
                                      .build();
	
		BaseActivity commActivity = new BaseActivity.Builder("community activity 1" + Helper.genDateBasedRand())
												.goal("community activity with an entry, todo and comment added to the todo ")
												.community(community)
												.build();
		
		BaseActivityToDo todo = BaseActivityToDo.builder("My ToDo " + Helper.genDateBasedRandVal())
                                                .description("GDPR data pop: Top-level ToDo " + Helper.genDateBasedRandVal())
                                                .addFile(Data.getData().file5)
                                                .assignTo(testUser1)
                                                .build();
		
		BaseActivityEntry entry = BaseActivityEntry.builder("My Entry " + Helper.genDateBasedRandVal())
                                                 .description("GDPR data pop - my test entry " + Helper.genDateBasedRandVal())
                                                 .addFile(Data.getData().file5)
                                                 .build();
			
		String commActivityTitle = "link=" + commActivity.getName();
		
		commUI.startTest();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner2);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI1);

		log.info("INFO: Add the 'Activities' widget to the Community using API");
		community.addWidgetAPI(comAPI1, apiCommOwner2, BaseWidget.ACTIVITIES);

		log.info("INFO: Add a community activity using the API");
		commActivity.createAPI(apiActOwner2, community);
		
		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		log.info("INFO: If guided tour dialog appears, close it");
		commUI.closeGuidedTourPopup();

		log.info("INFO: Open the Community");
		community.navViaUUID(commUI);
				
		log.info("INFO: Navigate to the community activity");
		commUI.clickLinkWait(commActivityTitle);		
				
		log.info("INFO: Add a ToDo to the community activity");		
		todo.create(activitiesUI);
		
		log.info("INFO: Get the ToDo UUID - to be used later");
		String todoUUID = activitiesUI.getEntryUUID(todo);  

		log.info("INFO: Log out as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour dialog displays, close it");
		commUI.closeGuidedTourPopup();
				
		log.info("INFO: Open the Community");
		community.navViaUUID(commUI);
				
		log.info("INFO: Navigate to the community activity");
		commUI.clickLinkWait(commActivityTitle);
		
		log.info("INFO: Refresh the browser");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Add an Entry to the community activity");		
		activitiesUI.createEntry(entry);
		
		log.info("INFO: UserA: " + testUser1.getDisplayName() + " edits the ToDo");
		log.info("INFO: Open the todo");
		activitiesUI.expandEntry(todoUUID);
		
		log.info("INFO: Click the ToDo Edit link");
		activitiesUI.clickLink(ActivitiesUIConstants.editEntry);

		log.info("INFO: Click on the ToDo More Options link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Add a tag to the ToDo");
		driver.getFirstElement(ActivitiesUIConstants.ToDo_InputText_Tags).type(Data.getData().TagForWikiPages);

		log.info("INFO: Save the tag to the ToDo - click Save button");
		activitiesUI.clickSaveButton();
		
		log.info("INFO: UserA: " + testUser1.getDisplayName() + " adds a comment to the top-level ToDo");
		activitiesUI.createCommentUnderEntry(todoUUID,Data.getData().BVT_Level3_ToDo_InputText_Title_Data);

		commUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Delete Community Activity</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA creates a community activity via API</li>
	 *<li><B>Step:</B> UserA adds the Activities app via API</li>
	 *<li><B>Step:</B> UserA navigates to the community activity</li>
	 *<li><B>Step:</B> UserA deletes the community activity</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void deleteCommunityActivity(){
		
		BaseCommunity community = new BaseCommunity.Builder("deleteCommunityActivity " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("GDPR data population - Put community activity into Trash")
                                      .build();
	
		BaseActivity commActivity = new BaseActivity.Builder("Community Activity - Delete Test" + Helper.genDateBasedRand())
												.goal("GDPR data pop - community activity will be deleted ")
												.community(community)
												.build();
					
		String commActivityTitle = "link=" + commActivity.getName();
		
		commUI.startTest();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner1);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI1);

		log.info("INFO: Add the 'Activities' widget to the Community using API");
		community.addWidgetAPI(comAPI1, apiCommOwner1, BaseWidget.ACTIVITIES);

		log.info("INFO: Add a community activity using the API");
		commActivity.createAPI(apiActOwner1, community);
		
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Open the Community");
		community.navViaUUID(commUI);
	
		log.info("INFO: Navigate to the community activity");
		commUI.clickLinkWait(commActivityTitle);
		
		log.info("INFO: Click on Activity Actions link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.activityActionMenu);
			
		log.info("INFO: From the Activity Actions menu select Delete Activity");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.actionMenuDeleteActivity);
		
		log.info("INFO: Click OK on the delete activity confirmation dialog");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);
		
		commUI.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Follow Community Activity</li>
	 *<li><B>Step:</B> UserB creates a community & adds UserA as a member</li>
	 *<li><B>Step:</B> UserB creates a community activity </li>
	 *<li><B>Step:</B> UserB adds the Activities app</li>
	 *<li><B>Step:</B> UserA follows to the community activity</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void followCommunityActivity(){
		
		BaseCommunity community = new BaseCommunity.Builder("followCommunityActivity " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                      .description("GDPR data population - Follow community activity test")
                                      .build();
	
		BaseActivity commActivity = new BaseActivity.Builder("community activity 1" + Helper.genDateBasedRand())
												.goal("GDPR data pop - community activity to be followed")
												.community(community)
												.build();
		
		String commActivityTitle = "link=" + commActivity.getName();
			
		commUI.startTest();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner2);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI1);

		log.info("INFO: Add the 'Activities' widget to the Community using API");
		community.addWidgetAPI(comAPI1, apiCommOwner2, BaseWidget.ACTIVITIES);

		log.info("INFO: Add a community activity using the API");
		commActivity.createAPI(apiActOwner2, community);
				
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Open the Community");
		community.navViaUUID(commUI);
				
		log.info("INFO: Navigate to the community activity");
		commUI.clickLinkWait(commActivityTitle);
		
		log.info("INFO: Click on the Following Actions link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.CommunityFolowMenu);
			
		log.info("INFO: Click on the Follow this Activity link");
		activitiesUI.clickLinkWait(ActivitiesUIConstants.CommunityActivityFollow);
	
		commUI.endTest();
		
	}

	
}
