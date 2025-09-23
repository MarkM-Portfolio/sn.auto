package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class ActivitiesDataPop<actAPI1> extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(ActivitiesDataPop.class);
	private TestConfigCustom cfg;
	private ActivitiesUI ui;	
	private APIActivitiesHandler apiOwner;
	private APICommunitiesHandler apiCommOwner;
	private String serverURL;
	private User testUser1, testUser2, testUser3, testUser4;
	private boolean isOnPremise;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser(); 
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		URLConstants.setServerURL(serverURL);
		
		//check environment to see if on-prem or on the cloud
				if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
					isOnPremise = true;
				} else {
					isOnPremise = false;
				}
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
			
		
	}
		
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population - Community with (2) community activities</li>
	 *<li><B>Step:</B> Create a Public community using the API</li>
	 *<li><B>Step:</B> Add the activity widget using the API </li>
	 *<li><B>Step:</B> Create (2) community activities using the API</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void createCommAddCommActivity() {
		
		BaseCommunity community = new BaseCommunity.Builder("createCommAddCommActivity " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Create a Public community.  Add (2) community activities ")
                                      .build();
	
		BaseActivity activity1 = new BaseActivity.Builder("test community activity 1 " + Helper.genDateBasedRand())
												.goal("1st community activity added to community: createCommAddCommActivity ")
												.community(community)
												.build();
		
		BaseActivity activity2 = new BaseActivity.Builder("test community activity 2 " + Helper.genDateBasedRand())
		                                         .goal("2nd community activity added to community: createCommAddCommActivity ")
		                                         .community(community)
		                                         .build();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner, comAPI1);

		log.info("INFO: Add the 'Activities' widget to the Community using API");
		community.addWidgetAPI(comAPI1, apiCommOwner, BaseWidget.ACTIVITIES);

		log.info("INFO: Add the 1st community activity using the API");
		activity1.createAPI(apiOwner, community);

		log.info("INFO: Add the 2nd community activity using the API");
		activity2.createAPI(apiOwner, community);

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population - Stand-alone Activity with multiple contributors</li>
	 *<li><B>Step:</B> Create a stand-alone activity & add (3) members (1 Owners & 2 Authors)</li>
	 *<li><B>Step:</B> The additional Owner will make an update to the activity</li>
	 *<li><B>Step:</B> 1st user with Author access will post an entry to the activity</li>
	 *<li><B>Step:</B> 2nd user with Author access will comment on the entry</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void multipleContributorsStandAloneActivity() {
		
		ActivityMember member = new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON);
		ActivityMember member1 = new ActivityMember(ActivityRole.AUTHOR, testUser3, ActivityMember.MemberType.PERSON);
		ActivityMember member2 = new ActivityMember(ActivityRole.AUTHOR, testUser4, ActivityMember.MemberType.PERSON);

		BaseActivity activity = new BaseActivity.Builder("multipleContributorsStandAloneActivity " + Helper.genDateBasedRand())
		                                        .goal("Create a stand alone activity.  Add (3) members." )
		                                        .addMember(member)
		                                        .addMember(member1)
		                                        .addMember(member2)
		                                        .build();
	
		BaseActivityEntry entry = BaseActivityEntry.builder("Test entry " + Helper.genDateBasedRandVal())
				                                   .tags(Helper.genDateBasedRandVal())
				                                   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				                                   .build();

		log.info("INFO: log into Activities as an Owner " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Create an Activity");
		activity.create(ui);
		
		log.info("INFO: Change the Activity access from Private to Public");
		this.makeActivityPublic(activity);

		log.info("INFO: log out the Owner " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);

		log.info("INFO: log into Activities as the additional Owner " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser2);

		log.info("INFO: The additional Owner " + testUser2.getDisplayName() + " will edit the Activity");
		log.info("INFO: Open the activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: Click on Activities tab");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);

		log.info("INFO: Edit the Activity goal from '" + activity.getGoal() + "' to '" + Data.getData().Activity_Name + "'");
		activity.editGoal(ui, Data.getData().Activity_Name);

		log.info("INFO: log out as the additional Owner " + testUser2.getDisplayName());
		ui.logout();
		ui.close(cfg);

		log.info("INFO: log into Activities as a user with Author access " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser3);

		log.info("INFO: Open the activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: User " + testUser3.getDisplayName() + " will create an activity entry");
		log.info("INFO: Create a new Entry for the Activity");
		entry.create(ui);
		String entryUUID = ui.getEntryUUID(entry);  
		ui.expandEntry(entryUUID);

		log.info("INFO: log out user " + testUser3.getDisplayName());
		ui.logout();
		ui.close(cfg);

		log.info("INFO: log into Activities the 2nd user with Author access " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser4);

		log.info("INFO: Open the activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: User " + testUser4.getDisplayName() + " will comment on the activity entry");
		ui.createCommentUnderEntry(entryUUID,Data.getData().commonComment);

		ui.endTest();
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Multiple users follow the activity</li>
	 *<li><B>Step:</B> Create an activity & add (3) members to the activity</li>
	 *<li><B>Step:</B> Each of the (3) members will follow the activity</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void multipleUsersFollowStandaloneActivity() {
		
		ActivityMember member = new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON);
		ActivityMember member1 = new ActivityMember(ActivityRole.AUTHOR, testUser3, ActivityMember.MemberType.PERSON);
		ActivityMember member2 = new ActivityMember(ActivityRole.AUTHOR, testUser4, ActivityMember.MemberType.PERSON);

		BaseActivity activity = new BaseActivity.Builder("multipleUsersFollowStandAloneActivity " + Helper.genDateBasedRand())
		                                        .goal("Create a stand alone activity.  Each of the (3) members will follow the activity." )
		                                        .addMember(member)
		                                        .addMember(member1)
		                                        .addMember(member2)
		                                        .build();
		
		log.info("INFO: log into Activities as an Owner " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Create an Activity");
		activity.create(ui);
		
		log.info("INFO: Change the Activity access from Private to Public");
		this.makeActivityPublic(activity);

		log.info("INFO: log out the Owner " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);
		
		log.info("INFO: log into Activities as the additional Owner " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser2);
		
		log.info("INFO: Open the activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		log.info("INFO: The additional Owner " + testUser2.getDisplayName() + " will Follow the Activity");		
		ui.clickLinkWait(ActivitiesUIConstants.followThisActivityLink);
		
		log.info("Logout as " + testUser2.getDisplayName());
		ui.logout();
		ui.close(cfg);
		
		log.info("INFO: log into Activities as " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser3);
		
		log.info("INFO: Open the activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		log.info("INFO: The 1st user with Author access " + testUser3.getDisplayName() + " will Follow the Activity");			
		ui.clickLinkWait(ActivitiesUIConstants.followThisActivityLink);
		
		log.info("Logout as " + testUser3.getDisplayName());
		ui.logout();
		ui.close(cfg);
		
		log.info("INFO: log into Activities as " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser4);
		
		log.info("INFO: Open the activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		log.info("INFO: The 2nd user with Author access " + testUser4.getDisplayName() + " will Follow the Activity");
		ui.clickLinkWait(ActivitiesUIConstants.followThisActivityLink);
		
		ui.logout();
		ui.close(cfg);
		
		ui.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Stand alone activity with (1) member added</li>
	 *<li><B>Step:</B> Create a stand alone activity</li>
	 *<li><B>Step:</B> Add (1) user to the activity</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void standAloneActivityWithMember() {
		
		ActivityMember member = new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON);
		
		BaseActivity activity = new BaseActivity.Builder("standAloneActivityWithMember " + Helper.genDateBasedRand())
                                   .goal("Create a stand alone activity with 1 member added" )
                                   .addMember(member)                               
                                   .build();		
				
		log.info("INFO: Log into Activities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Creating a new Activity: " + activity.getName());
		activity.create(ui);
		
		log.info("INFO: Change the Activity access from Private to Public");
		this.makeActivityPublic(activity);
				
		ui.endTest();
	}
	
	/**
	* The makeActivityPublic method will change the default access
	* from Private to Public and then verify the change was successful
	*/		
	
	private void makeActivityPublic(BaseActivity activity){
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

	}
}
