package com.ibm.conn.auto.tests.activities.regression;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
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
import com.ibm.conn.auto.tests.communities.regression.Regression_Communities;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class FVTRegressionActivitiesOthers extends SetUpMethods2 {

	protected static Logger log = LoggerFactory
			.getLogger(Regression_Communities.class);
	protected ActivitiesUI ui;
	protected TestConfigCustom cfg;
	protected User internalUser1, internalUser2, internalUser3, externalUser1;
	protected APIActivitiesHandler apiOwner;

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);

		internalUser1 = cfg.getUserAllocator().getUser();
		internalUser2 = cfg.getUserAllocator().getUser();
		internalUser3 = cfg.getUserAllocator().getUser();
		externalUser1 = cfg.getUserAllocator().getGroupUser("external_group");
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig
				.getBrowserURL());
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL,
				internalUser1.getAttribute(cfg.getLoginPreference()), internalUser1.getPassword());
	}
	/**
	 * <ul>
	 * <li><B>defect 133674</B></li>
	 * <li><B>create a moderated community</B></li>
	 * <li><B>add an activity in the community</B></li>
	 * <li><B>login with another user within the same org but not the member of
	 * the community</B></li>
	 * <li><B>goto community activity view</B></li>
	 * <li><B>verify: the user cannot see the activity</B></li>
	 * </ul>
	 */
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})
	public void noneMemberNotSeeCommAct() {

		String testName = ui.startTest();
		User communityOwner = internalUser1;
		User anotherUser = internalUser2;
		
		log.info("INFO: create a Restrict external community");		
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(APIUtils.formatBrowserURLForAPI(testConfig
				.getBrowserURL()), communityOwner.getAttribute(cfg.getLoginPreference()), communityOwner.getPassword());		
		BaseCommunity community1 = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
												.shareOutside(false)   
												.access(Access.MODERATED)
												.build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community1.createAPI(comApiOwner);
		
		community1.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);
		
		BaseActivity activity = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community1)
				.build();
        
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(communityOwner);

		log.info("INFO: Create an activity");
		activity.createInCommunity(ui);
		
		ui.reloginActivities(anotherUser);
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(activity.getName()),"Error: user "+anotherUser.getDisplayName()+" should not see activity "+activity.getName());
		
		ui.endTest();
	}	
	/**
	 * <ul>
	 * <li><B>defect 140714</B></li>
	 * <li><B>create an external community</B></li>
	 * <li><B>add a guest user</B></li>
	 * <li><B>add an external user</B></li>
	 * <li><B>add Activities widget</B></li>
	 * <li><B>add an activity, set all community members as individual author</B></li>
	 * <li><B>Verify:see external label for external members in activity member page</B></li>
	 * <li><B>log in the community with the guest member</B></li>
	 * <li><B>Verify:the guest user is able to create new activity</B></li>
	 * <li><B>Verify:the guest can access & work with the existent activity.</B></li>
	 * <li><B>log in the community with the external member</B></li>
	 * <li><B>Verify:the external user is able to create new activity</B></li>
	 * <li><B>Verify:the external user can access & work with the existent
	 * activity.</B></li>
	 * </ul>
	 */
	@Test(groups = { "regressioncloud", "fvtcloud"})
	public void guestExternalCreateComAct1(){
		String testName = ui.startTest();
		CommunitiesUI comui;
		comui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		User communityOwner = internalUser1;
		User externalUser = externalUser1;
		User guestUser = cfg.getUserAllocator().getGuestUser();
		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .shareOutside(true)
												   .addexMember(new Member(CommunityRole.MEMBERS,externalUser))
												   .addexMember(new Member(CommunityRole.MEMBERS,guestUser))
												   .build();
		
		comui.loadComponent(Data.getData().ComponentCommunities);		
		comui.login(communityOwner);
		
		community.create(comui);
		community.addWidget(comui, BaseWidget.ACTIVITIES);
   

		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.addMember(new ActivityMember(ActivityRole.AUTHOR, externalUser, ActivityMember.MemberType.PERSON))
				.addMember(new ActivityMember(ActivityRole.AUTHOR, guestUser, ActivityMember.MemberType.PERSON))			
				.build();
		log.info("INFO: Create community");
		activity1.createInCommunity(ui);
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);

		Assert.assertTrue(ui.fluentWaitPresent("css=div[title='"+externalUser.getDisplayName()+"'] ~ div[class='externalPerson']:contains('(external)')"),"Error: no see external lable for the external user");
		Assert.assertTrue(ui.fluentWaitPresent("css=div[title='"+guestUser.getDisplayName()+"'] ~ div[class='externalPerson']:contains('(external)')"),"Error: no see external lable for the guest user");

		log.info("INFO: log in the community with the guest member, the guest user is able to create new activity, the guest can access & work with the existent activity. ");
		 
		ui.reloginActivities(guestUser);
		
		BaseActivity activity2 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();
		
		activity2.createInCommunity(ui, CommunityRole.MEMBERS);
		Assert.assertTrue(ui.fluentWaitTextPresent("Activity "+activity2.getName()+" successfully created"),"Error: fail to create community activity as guest member");
		
		ui.openCommunityActivity(activity1);
		
		BaseActivityEntry entry1 = BaseActivityEntry
		.builder(testName + "Entry" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry1);

		Assert.assertTrue(ui.fluentWaitTextPresent(entry1.getTitle()),"Error: cannot find the new entry "+entry1.getTitle());

		log.info("INFO: log in the community with the external member, the external user is able to create new activity, the external can access & work with the existent activity. ");		
		ui.reloginActivities(externalUser);

		BaseActivity activity3 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();
		
		activity3.createInCommunity(ui, CommunityRole.MEMBERS);
		Assert.assertTrue(ui.fluentWaitTextPresent("Activity "+activity3.getName()+" successfully created"),"Error: fail to create community activity as external member");		
		
		ui.openCommunityActivity(activity1);		
		BaseActivityEntry entry2 = BaseActivityEntry
		.builder(testName + "Entry" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry2);
		Assert.assertTrue(ui.fluentWaitTextPresent(entry2.getTitle()),"Error: cannot find the new entry "+entry2.getTitle());		
		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>defect 140714</B></li>
	 * <li><B>create an external community</B></li>
	 * <li><B>add a guest user</B></li>
	 * <li><B>add an external user</B></li>
	 * <li><B>add Activities widget</B></li>
	 * <li><B>add an activity, set all community members as implicit role author</B></li>
	 * <li><B>log in the community with the guest member</B></li>
	 * <li><B>Verify:the guest user is able to create new activity</B></li>
	 * <li><B>Verify:the guest can access & work with the existent activity.</B></li>
	 * <li><B>log in the community with the external member</B></li>
	 * <li><B>Verify:the external user is able to create new activity</B></li>
	 * <li><B>Verify:the external user can access & work with the existent
	 * activity.</B></li>
	 * </ul>
	 */
	@Test(groups = { "regressioncloud", "fvtcloud"})
	public void guestExternalCreateComAct2(){
		String testName = ui.startTest();
		CommunitiesUI comui;
		comui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		User communityOwner = internalUser1;
		User externalUser = externalUser1;
		User guestUser = cfg.getUserAllocator().getGuestUser();
		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .shareOutside(true)
												   .addexMember(new Member(CommunityRole.MEMBERS,externalUser))
												   .addexMember(new Member(CommunityRole.MEMBERS,guestUser))
												   .build();
		
		comui.loadComponent(Data.getData().ComponentCommunities);		
		comui.login(communityOwner);
		log.info("INFO: Create community");		
		community.create(comui);
		community.addWidget(comui, BaseWidget.ACTIVITIES);
   

		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();

		activity1.createInCommunity(ui);

		log.info("INFO: log in the community with the guest member, the guest user is able to create new activity, the guest can access & work with the existent activity. ");		
		ui.reloginActivities(guestUser);
		
		BaseActivity activity2 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();
		
		activity2.createInCommunity(ui, CommunityRole.MEMBERS);
		Assert.assertTrue(ui.fluentWaitTextPresent("Activity "+activity2.getName()+" successfully created"),"Error: fail to create community activity as guest member");
		
		ui.openCommunityActivity(activity1);
		
		BaseActivityEntry entry1 = BaseActivityEntry
		.builder(testName + "Entry" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry1);

		Assert.assertTrue(ui.fluentWaitTextPresent(entry1.getTitle()),"Error: cannot find the new entry "+entry1.getTitle());
		
		log.info("INFO: log in the community with the external member, the external user is able to create new activity, the external can access & work with the existent activity.");
		ui.reloginActivities(externalUser);

		BaseActivity activity3 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();
		
		activity3.createInCommunity(ui, CommunityRole.MEMBERS);
		Assert.assertTrue(ui.fluentWaitTextPresent("Activity "+activity3.getName()+" successfully created"),"Error: fail to create community activity as external member");		
		
		ui.openCommunityActivity(activity1);		
		BaseActivityEntry entry2 = BaseActivityEntry
		.builder(testName + "Entry" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry2);
		Assert.assertTrue(ui.fluentWaitTextPresent(entry2.getTitle()),"Error: cannot find the new entry "+entry2.getTitle());		

		
		ui.endTest();
	}	
	/**
	 * <ul>
	 * <li><B>create an external activity
	 * <li><B>add a guest user as author
	 * <li><B>add an external user as author
	 * <li><B>Verify:see external label for external members in activity member page</B></li>
	 * <li><B>log in the activity with the guest member
	 * <li><B>Verify: the guest user is able to create new entries/todos in the
	 * activity.
	 * <li><B>log in the activity with the external member
	 * <li><B>Verify: the guest user is able to create new entries/todos in the
	 * activity.
	 * </ul>
	 */
	@Test(groups = { "regressioncloud", "fvtcloud"})
	public void guestExternalWorkInAct(){
		String testName = ui.startTest();
		
		User activityOwner = internalUser1;
		User externalUser = externalUser1;
		User guestUser = cfg.getUserAllocator().getGuestUser();
		BaseActivity externalActivity = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.tags(Data.getData().Start_An_Activity_InputText_Tags_Data
						+ Helper.genDateBasedRand())
				.shareExternal(true)
				.addExternalMember(new ActivityMember(ActivityRole.AUTHOR, externalUser, ActivityMember.MemberType.PERSON))
				.addExternalMember(new ActivityMember(ActivityRole.AUTHOR, guestUser, ActivityMember.MemberType.PERSON))				
				.build();

		log.info("INFO: Create an external activity");
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(activityOwner);
		
		externalActivity.create(ui);
		
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Members);
		Assert.assertTrue(ui.fluentWaitPresent("css=div[title='"+externalUser.getDisplayName()+"'] ~ div[class='externalPerson']:contains('(external)')"),"Error: no see external lable for the external user");
		Assert.assertTrue(ui.fluentWaitPresent("css=div[title='"+guestUser.getDisplayName()+"'] ~ div[class='externalPerson']:contains('(external)')"),"Error: no see external lable for the guest user");		

		log.info("INFO: log in the activity with the external member, the external user is able to create new entries/todos in the activities");
		ui.reloginActivity(externalUser, externalActivity);
		
		BaseActivityEntry entry1 = BaseActivityEntry
		.builder(testName + "Entry" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry1);
		Assert.assertTrue(ui.fluentWaitTextPresent(entry1.getTitle()),"Error: cannot find the new entry "+entry1.getTitle());
		
		log.info("INFO: log in the activity with the guest member, the guest user is able to create new entries/todos in the activities");		
		ui.reloginActivity(guestUser, externalActivity);
		
		BaseActivityEntry entry2 = BaseActivityEntry
		.builder(testName + "Entry" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry2);	
		Assert.assertTrue(ui.fluentWaitTextPresent(entry2.getTitle()),"Error: cannot find the new entry "+entry2.getTitle());		
		ui.endTest();		
	}	
}
