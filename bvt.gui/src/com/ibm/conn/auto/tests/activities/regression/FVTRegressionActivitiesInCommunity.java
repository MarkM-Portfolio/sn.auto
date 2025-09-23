package com.ibm.conn.auto.tests.activities.regression;

import java.io.File;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;

import com.ibm.conn.auto.appobjects.member.ActivityMember;

import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.communities.regression.Regression_Communities;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class FVTRegressionActivitiesInCommunity extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(Regression_Communities.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private String serverURL;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:create community activity in different Community types</B></li>
	 * <li><B>create a Restrict external community</B></li>
	 * <li><B>add activity widget and add an activity</B></li>
	 * <li><B>check in the Community Activities view</B></li>
	 * <li><B>verify: see Shared Externally for this activity</B></li>
	 * <li><B>create a restrict internal community</B></li>
	 * <li><B>add activity widget and add an activity</B></li>
	 * <li><B>check in the Community Activities view</B></li>
	 * <li><B>verify: no text Shared Externally for this activity</B></li>
	 * <li><B>create a Moderated community</B></li>
	 * <li><B>add activity widget and add an activity</B></li>
	 * <li><B>check in the Community Activities view</B></li>
	 * <li><B>verify: no text Shared Externally for this activity</B></li>
	 * <li><B>create an Open community</B></li>
	 * <li><B>add activity widget and add an activity</B></li>
	 * <li><B>check in the Community Activities view</B></li>
	 * <li><B>verify: no text Shared Externally for this activity</B></li>
	 * </ul>
	 */
	@Test(groups = { "regressioncloud", "fvtcloud"})
	public void inExActivityInCommunity() {

		String testName = ui.startTest();
		User communityOwner = cfg.getUserAllocator().getUser();
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, communityOwner.getAttribute(cfg.getLoginPreference()), communityOwner.getPassword());		
		BaseCommunity community1 = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
					.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
					.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
					.access(Access.RESTRICTED)
					.description("Test description for testcase " + testName)
					.shareOutside(true)
					.build();
		
		log.info("INFO: create a Restrict external community using API");
		Community comAPI = community1.createAPI(comApiOwner);
		
		community1.getCommunityUUID_API(comApiOwner, comAPI);
		
		log.info("INFO: Add Activity widget with api");
		community1.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);
		
		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community1)
				.implicitRole(ActivityRole.OWNER)
				.build();
        
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(communityOwner);

		activity1.createInCommunity(ui);

		log.info("INFO: check in the Community Activities view, see Shared Externally for this activity");
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		
		Assert.assertTrue(
				ui.fluentWaitPresent(ui.getActivityExternalLabel(activity1)),
				"ERROR: The External activity icon was not present.");
		

		BaseCommunity community2 = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .shareOutside(false)
												   .build();
		
		log.info("INFO: create a restrict internal community");
		comAPI = community2.createAPI(comApiOwner);
		
		community2.getCommunityUUID_API(comApiOwner, comAPI);
		
		community2.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);
		
		BaseActivity activity2 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community2)
				.implicitRole(ActivityRole.READER)
				.build();
		
		log.info("INFO: Create an activity");       
		activity2.createInCommunity(ui);
		log.info("INFO: Check in the Community Activities view,see Shared Externally for this activity");
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		
		Boolean isTextNotPresent = ui.isTextNotPresentWithinElement(ui.getFirstVisibleElement(
				"css=h4[id^='" + ui.getActivityUUID(activity2) + "']"), "Shared Externally");
		Assert.assertTrue(isTextNotPresent,
				"ERROR: Shared Externally was present for internal Activity.");		
		
		BaseCommunity community3 = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
		   .access(Access.MODERATED)
		   .build();

		log.info("INFO: create a Moderated community");
		comAPI = community3.createAPI(comApiOwner);
		
		community3.getCommunityUUID_API(comApiOwner, comAPI);
		
		community3.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);
		
		BaseActivity activity3 = new BaseActivity.Builder(testName
		+ Helper.genDateBasedRand())
		.community(community3)
		.implicitRole(ActivityRole.AUTHOR)
		.build();
		
		log.info("INFO: Create an activity");
		activity3.createInCommunity(ui);

		log.info("INFO: Select activities tab,see Shared Externally for this activity");
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		
		isTextNotPresent = ui.isTextNotPresentWithinElement(ui.getFirstVisibleElement(
		"css=h4[id^='" + ui.getActivityUUID(activity3) + "']"), "Shared Externally");
		Assert.assertTrue(isTextNotPresent,
		"ERROR: Shared Externally was present for internal Activity.");		

//		 * <li><B>create an Open community
//		 * <li><B>add activity widget and add an activity
//		 * <li><B>check in the Community Activities view
//		 * <li><B>verify: no text Shared Externally for this activity
		
		BaseCommunity community4 = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
		   .access(Access.PUBLIC)
		   .build();

		//create community
		log.info("INFO: create an Open community");
		comAPI = community4.createAPI(comApiOwner);
		
		community4.getCommunityUUID_API(comApiOwner, comAPI);
		
		community4.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);
		
		BaseActivity activity4 = new BaseActivity.Builder(testName
		+ Helper.genDateBasedRand())
		.community(community4)
		.implicitRole(ActivityRole.AUTHOR)
		.build();
		
		log.info("INFO: Create an activity");
		activity4.createInCommunity(ui);
		log.info("INFO: check in the Community Activities view, see Shared Externally for this activity");
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		
		isTextNotPresent = ui.isTextNotPresentWithinElement(ui.getFirstVisibleElement(
		"css=h4[id^='" + ui.getActivityUUID(activity4) + "']"), "Shared Externally");
		Assert.assertTrue(isTextNotPresent,
		"ERROR: Shared Externally was present for internal Activity.");		
		
		ui.endTest();

	}
	/**
	 * <ul>
	* <li><B>info:communities activity membership management</B></li>	
	* <li><B>create a community with userA, add 2 members userB and userC</B></li>
	* <li><B>use userB to create an activity with implicit owner role</B></li>
	* <li><B>login the activity with userC</B></li>
	* <li><B>verify: userC has the owner authority in the activity</B></li>
	* <li><B>use userC to create an activity with implicit author role</B></li>
	* <li><B>login the activity with userB</B></li>
	* <li><B>verify: userB has the author authority in the activity</B></li>
	* <li><B>use userB to create an activity with implicit reader role</B></li>
	* <li><B>login the activity with userC</B></li>
	* <li><B>verify: userC has the reader authority in the activity</B></li>
	* <li><B>use userA to create an activity with explicit owner role to userB</B></li>
	* <li><B>login the activity with userB</B></li>
	* <li><B>verify: userB has the owner authority in the activity</B></li>
	* <li><B>use userB to create an activity with explicit author role to userC</B></li>
	* <li><B>login the activity with userC</B></li>
	* <li><B>verify: userC has the author authority in the activity</B></li>
	* <li><B>use userC to create an activity with explicit reader role to userB</B></li>
	* <li><B>login the activity with userB</B></li>
	* <li><B>verify: userB has the reader authority in the activity</B></li>
	* </ul>
	 */
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})	
	public void communityActivityMembership(){

		//		implicit owner
		//		--add members button
		//		--two options available, first is the default
		//		--owner option is the top
		//
		//		implicit author
		//		--no add members button
		//
		//
		//		explicit owner
		//		--add members button
		//		--two options available, second is the default
		//		--owner option is the top
		//
		//		explicit author
		//		--add members button
		//		--only second option available
		//		--author option is the top

		//		* <li><B>create a community with userA, add 2 members userB and userC</B></li>
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User communityNewOwner = cfg.getUserAllocator().getUser();
		User communityNewMember1;
		User communityNewMember2;
		BaseCommunity community;
		if(cfg.getProductName().toLowerCase().equals("cloud")){
			communityNewMember1 = cfg.getUserAllocator().getGroupUser("external_group");
			User guestUser1 = cfg.getUserAllocator().getGuestUser();
			communityNewMember2 = guestUser1;

			CommunitiesUI comui = CommunitiesUI.getGui(cfg.getProductName(), driver);

			community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
					.access(Access.RESTRICTED)
					.shareOutside(true)
					.addexMember(new Member(CommunityRole.MEMBERS,communityNewMember1))
					.addexMember(new Member(CommunityRole.MEMBERS,communityNewMember2))
					.build();

			logger.strongStep("Open browser and login to Communities as: " + communityNewOwner);
			log.info("INFO: Open browser and login to Communities as: " + communityNewOwner);
			comui.loadComponent(Data.getData().ComponentCommunities);		
			comui.login(communityNewOwner);

			logger.strongStep("INFO: Create Community from UI");
			log.info("INFO: Create Community from UI");
			community.create(comui);

			logger.strongStep("INFO: Add Activity widget from Community");
			log.info("INFO: Add Activity widget from Community");
			community.addWidget(comui, BaseWidget.ACTIVITIES);

			logger.strongStep("INFO: Relogin with "+ communityNewMember1);
			log.info("INFO: Relogin with "+ communityNewMember1);
			ui.reloginActivities(communityNewMember1);
		}else{
			communityNewMember1 = cfg.getUserAllocator().getUser();
			communityNewMember2 = cfg.getUserAllocator().getUser();
			APICommunitiesHandler comApiOwner;
			comApiOwner = new APICommunitiesHandler(serverURL, communityNewOwner.getAttribute(cfg.getLoginPreference()), communityNewOwner.getPassword());		
			community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
					.access(Access.RESTRICTED)
					.shareOutside(false)
					.addMember(new Member(CommunityRole.MEMBERS,communityNewMember1))
					.addMember(new Member(CommunityRole.MEMBERS,communityNewMember2))
					.build();

			logger.strongStep("INFO: Create restricted community using API");
			log.info("INFO: Create restricted community using API");
			Community comAPI = community.createAPI(comApiOwner);

			logger.strongStep("INFO: Create Activity Widget in restricted community using API");
			log.info("INFO: Create Activity Widget in restricted community using APII");
			community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);

			logger.strongStep("INFO: Open browser and login to Activity as: " + communityNewMember1);
			log.info("INFO: Open browser and login to Activity as: " + communityNewMember1);
			ui.loadComponent(Data.getData().ComponentActivities);
			ui.login(communityNewMember1);	
		}		

		//		implicit owner
		//		--add members button
		//		--two options available, first is the default
		//		--owner option is the top
		logger.strongStep("INFO: use userB to create an activity with implicit owner role");
		log.info("INFO: use userB to create an activity with implicit owner role");
		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.OWNER)
				.build();

		logger.strongStep("INFO: Create an activity inside Community");
		log.info("INFO: Create an activity inside Community");
		activity1.createInCommunity(ui, CommunityRole.MEMBERS);

		logger.strongStep("INFO: Logout and login to activity1 as:" + communityNewMember2);
		log.info("INFO: Logout and login to activity1 as:" + communityNewMember2);
		ui.reloginCommunityActivity(communityNewMember2, activity1);

		logger.strongStep("INFO: Verify AddSection, AddToDo and NewEntry buttons on Activity page");
		log.info("INFO: Verify AddSection, AddToDo and NewEntry buttons on Activity page");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.AddSection)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.AddToDo)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.New_Entry),"ERROR: No Add Section/Entry/Todo for activity Owner");

		logger.strongStep("INFO: Check owner can add new member as owner");
		log.info("INFO: Check owner can add new member as owner");		
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		ui.clickLinkWait(ActivitiesUIConstants.AddMembersButton);

		Element implicitRoleOption = ui.getFirstVisibleElement(ActivitiesUIConstants.CommunityActivityImplicitRoleOption);
		Assert.assertTrue(implicitRoleOption!=null && !"true".equals(implicitRoleOption.getAttribute("disabled")),"Error: cannot find implicit role option or the option isn't enable for owner");

		Element explicitRoleOption = ui.getFirstVisibleElement(ActivitiesUIConstants.CommunityActivityExplicitRoleOption);

		Assert.assertTrue(explicitRoleOption!=null && !"true".equals(explicitRoleOption.getAttribute("disabled")),"Error: cannot find implicit role option or the option isn't enable");

		List<Element> roleOptions = ui.getFirstVisibleElement(ActivitiesUIConstants.StartCommunityActivityImplicitRole).useAsDropdown().getOptions();
		Assert.assertTrue(roleOptions.get(0).isTextPresent("Owner"),"ERROR: No owner role can be selected when adding new member by activity owner");			

		//		implicit author
		//		--no add members button		
		logger.strongStep("INFO: use userC to create an activity with implicit author role");
		log.info("INFO: use userC to create an activity with implicit author role");	
		BaseActivity activity2 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();

		logger.strongStep("INFO: Create an Activity inside Community");
		log.info("INFO: Create an Activity inside Community");	
		activity2.createInCommunity(ui, CommunityRole.MEMBERS);

		logger.strongStep("INFO: Logout and relogin to activity page as :" + communityNewMember1);
		log.info("INFO: Logout and relogin to activity page as :" + communityNewMember1);	
		ui.reloginCommunityActivity(communityNewMember1, activity2);

		logger.strongStep("INFO: Verify AddSection, AddToDo and NewEntry button on activity page");
		log.info("INFO: Verify AddSection, AddToDo and NewEntry button on activity page");	
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.AddSection)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.AddToDo)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.New_Entry),"ERROR: No Add Section/Entry/Todo for activity author");

		logger.strongStep("INFO: Navigate to Members page and verify that Add Members text is not present");
		log.info("INFO: Navigate to Members page and verify that Add Members text is not present");	
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		Assert.assertTrue(driver.isTextNotPresent("Add Members"),"ERROR: See Add Members button for activity Reader");

		logger.strongStep("INFO: use userB to create an activity with implicit reader role");
		log.info("INFO: use userB to create an activity with implicit reader role");			
		BaseActivity activity3 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.READER)
				.build();

		logger.strongStep("INFO: Create an Activity3 inside Community");
		log.info("INFO: Create an Activity3 inside Community");	
		activity3.createInCommunity(ui, CommunityRole.MEMBERS);

		logger.strongStep("INFO: Logout and relogin to activity3 page as :" + communityNewMember2);
		log.info("INFO: Logout and relogin to activity3 page as :" + communityNewMember2);	
		ui.reloginCommunityActivity(communityNewMember2, activity3);
		ui.fluentWaitTextPresent(activity3.getName());

		logger.strongStep("INFO: Verify that Add Section text is not present");
		log.info("INFO: Verify that Add Section text is not present");	
		driver.changeImplicitWaits(5);
		Assert.assertTrue(driver.isTextNotPresent("Add Section"),"ERROR: See Add Section/Entry/Todo for activity Reader");
		driver.turnOnImplicitWaits();

		logger.strongStep("INFO: Navigate to Members page and verify that Add Members text is not present");
		log.info("INFO: Navigate to Members page and verify that Add Members text is not present");	
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		Assert.assertTrue(driver.isTextNotPresent("Add Members"),"ERROR: See Add Members button for activity Reader");	

		//		explicit owner
		//		--add members button
		//		--two options available, second is the default
		//		--owner option is the top
		logger.strongStep("INFO: Logout and relogin to activity page as :" + communityNewOwner);
		log.info("INFO: Logout and relogin to activity page as :" + communityNewOwner);		
		ui.reloginActivities(communityNewOwner);
		ui.waitForPageLoaded(driver);
		BaseActivity activity4 = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				.community(community)
				.addMember(new ActivityMember(ActivityRole.OWNER,communityNewMember1, ActivityMember.MemberType.PERSON))
				.build();

		logger.strongStep("INFO: Create an Activity4 inside Community");
		log.info("INFO: Create an Activity4 inside Community");	
		activity4.createInCommunity(ui);

		logger.strongStep("INFO: Logout and relogin to Activity4 page as :" + communityNewMember1);
		log.info("INFO: Logout and relogin to Activity4 page as :" + communityNewMember1);
		ui.reloginCommunityActivity(communityNewMember1, activity4);

		logger.strongStep("INFO: Verify AddSection, AddToDo and NewEntry button on activity page");
		log.info("INFO: Verify AddSection, AddToDo and NewEntry button on activity page");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.AddSection)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.AddToDo)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.New_Entry),"ERROR: No Add Section/Entry/Todo for activity Owner");

		logger.strongStep("INFO: Check owner can add new member as owner");
		log.info("INFO: Check owner can add new member as owner");		
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		ui.clickLinkWait(ActivitiesUIConstants.AddMembersButton);
		implicitRoleOption = ui.getFirstVisibleElement(ActivitiesUIConstants.CommunityActivityImplicitRoleOption);
		Assert.assertTrue(implicitRoleOption!=null && !"true".equals(implicitRoleOption.getAttribute("disabled")),"Error: cannot find implicit role option or the option isn't enable for owner");

		explicitRoleOption = ui.getFirstVisibleElement(ActivitiesUIConstants.CommunityActivityExplicitRoleOption);

		Assert.assertTrue(explicitRoleOption!=null && !"true".equals(explicitRoleOption.getAttribute("disabled"))&& explicitRoleOption.isSelected(),"Error: cannot find implicit role option or the option isn't enable");

		roleOptions = ui.getFirstVisibleElement(ActivitiesUIConstants.StartCommunityActivityExplicitRole).useAsDropdown().getOptions();
		Assert.assertTrue(roleOptions.get(0).isTextPresent("Owner"),"ERROR: No owner role can be selected when adding new member by activity owner");					

		//		explicit author
		//		--add members button
		//		--only second option available
		//		--author option is the top
		logger.strongStep("INFO: use userB to create an activity with explicit author role to userC");
		log.info("INFO: use userB to create an activity with explicit author role to userC");
		BaseActivity activity5 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.addMember(new ActivityMember(ActivityRole.AUTHOR,communityNewMember2, ActivityMember.MemberType.PERSON))
				.build();

		logger.strongStep("INFO: Create an Activity5 inside Community");
		log.info("INFO: Create an Activity5 inside Community");	
		activity5.createInCommunity(ui, CommunityRole.MEMBERS); 

		logger.strongStep("INFO: Logout and relogin to Activity5 page as :" + communityNewMember2);
		log.info("INFO: Logout and relogin to Activity5 page as :" + communityNewMember2);
		ui.reloginCommunityActivity(communityNewMember2, activity5);

		logger.strongStep("INFO: Verify AddSection, AddToDo and NewEntry buttons on Activity page");
		log.info("INFO: Verify AddSection, AddToDo and NewEntry buttons on Activity page");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.AddSection)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.AddToDo)&&
				ui.fluentWaitElementVisible(ActivitiesUIConstants.New_Entry),"ERROR: No Add Section/Entry/Todo for activity Owner");
		log.info("INFO: Check owner can add new member as owner");		

		logger.strongStep("INFO: Check owner can add new member as owner");
		log.info("INFO: Check owner can add new member as owner");	
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		ui.clickLinkWait(ActivitiesUIConstants.AddMembersButton);
		implicitRoleOption = ui.getFirstVisibleElement(ActivitiesUIConstants.CommunityActivityImplicitRoleOption);
		Assert.assertTrue(implicitRoleOption!=null && "true".equals(implicitRoleOption.getAttribute("disabled")),"Error: cannot find implicit role option or the option isn't enable for owner");

		explicitRoleOption = ui.getFirstVisibleElement(ActivitiesUIConstants.CommunityActivityExplicitRoleOption);

		Assert.assertTrue(explicitRoleOption!=null && !"true".equals(explicitRoleOption.getAttribute("disabled"))&& explicitRoleOption.isSelected(),"Error: cannot find implicit role option or the option isn't enable");

		roleOptions = ui.getFirstVisibleElement(ActivitiesUIConstants.StartCommunityActivityExplicitRole).useAsDropdown().getOptions();
		Assert.assertTrue(roleOptions.get(0).isTextPresent("Author"),"ERROR: Author role is not the first option when adding new member by activity author");

		log.info("INFO: use userC to create an activity with explicit reader role to userB");	
		BaseActivity activity6 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.addMember(new ActivityMember(ActivityRole.READER,communityNewMember1, ActivityMember.MemberType.PERSON))
				.build();

		logger.strongStep("INFO: Create an Activity6 inside Community");
		log.info("INFO: Create an Activity6 inside Community");	
		activity6.createInCommunity(ui, CommunityRole.MEMBERS);     

		logger.strongStep("INFO: Logout and relogin to Activity6 page as :" + communityNewMember1);
		log.info("INFO: Logout and relogin to Activity6 page as :" + communityNewMember1);	
		ui.reloginCommunityActivity(communityNewMember1, activity6);
		ui.fluentWaitTextPresent(activity6.getName());

		logger.strongStep("INFO: Verify that Add Section text is not present");
		log.info("INFO: Verify that Add Section text is not present");
		driver.changeImplicitWaits(5);
		Assert.assertTrue(driver.isTextNotPresent("Add Section"),"ERROR: See Add Section/Entry/Todo for activity Reader");
		driver.turnOnImplicitWaits();

		logger.strongStep("INFO: Navigate to Members page and verify that Add Members text is not present");
		log.info("INFO: Navigate to Members page and verify that Add Members text is not present");	
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		Assert.assertTrue(driver.isTextNotPresent("Add Members"),"ERROR: See Add Members button for activity Reader");			

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: Create all basic data in Community Activity</B></li>
	 * <li><B>Step: Create a community with 2 members</B></li>
	 * <li><B>Step: Create a activity and assign all community members as author</B></li>
	 * <li><B>Step: login with author.</B></li>
	 * <li><B>Step: create a section.</B></li>	 
	 * <li><B>Step: create an entry under the section with all customer fields.</B></li>
	 * <li><B>Step: create an todo under the section with all customer fields, include multiple assignees.</B></li>
	 * <li><B>Step: create a todo under root</B></li>
	 * <li><B>verify no add entry under the todo.</B></li>
	 * <li><B>Step: create another todo under todo just created.</B></li>
	 * <li><B>Step: create an entry under root.</B></li>
	 * <li><B>Verify: no add entry under the todo</B></li>
	 * <li><B>create a todo under the entry just created</B></li>
	 * <li><B>Step: As reader.</B></li>
	 * <li><B>Step: create a comment for a todo under root.</B></li>
	 * <li><B>Step: create a comment for a entry under root.</B></li>
	 * </ul>
	 */
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})
	public void createAllBasicItems() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User communityOwner = cfg.getUserAllocator().getUser();
		User communityMember1 = cfg.getUserAllocator().getUser();
		User communityMember2 = cfg.getUserAllocator().getUser();
	
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, communityOwner.getAttribute(cfg.getLoginPreference()), communityOwner.getPassword());		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .shareOutside(false)
												   .addMember(new Member(CommunityRole.MEMBERS,communityMember1))
												   .addMember(new Member(CommunityRole.MEMBERS,communityMember2))
												   .build();
		
		logger.strongStep("INFO: create a Restrict external community");
		log.info("INFO: create a Restrict external community");
		Community comAPI = community.createAPI(comApiOwner);
		
		logger.strongStep("INFO: Add Activity widget using api to external community");
		log.info("INFO: Add Activity widget using api to external community");
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);

		logger.strongStep("INFO: use userB to create an activity with implicit owner role");
		log.info("INFO: use userB to create an activity with implicit owner role");		
		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();
		
		logger.strongStep("INFO: Open browser and login to Activities as: " + communityMember1);
		log.info("INFO: Open browser and login to Activities as: " + communityMember1);
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(communityMember1);
		
		logger.strongStep("INFO: Create an Activity1 inside Community");
		log.info("INFO: Create an Activity1 inside Community");
		activity1.createInCommunity(ui, CommunityRole.MEMBERS);
		
		logger.strongStep("INFO: Create a file and folder using api for " + communityMember2);
		log.info("INFO: Create a file and folder using api for " + communityMember2);
		String linkToFile = createFile(communityMember2);
		String linkToFolder = createFolder(communityMember2);

		logger.strongStep("INFO: Logout and relogin to Activity1 as " + communityMember2);
		log.info("INFO: Logout and relogin to Activity1 as " + communityMember2);				
		ui.reloginCommunityActivity(communityMember2, activity1);
		
		logger.strongStep("INFO: create section inside activity");
		log.info("INFO: create section inside activity");				
		String sectionTitle = Data.getData().Section_InputText_Title_Data
				+ Helper.genDateBasedRandVal();
		ui.addSection(sectionTitle);
		
		String sectionID = ui.getSectionUUIDByName(sectionTitle);
		logger.strongStep("INFO: click on entry under the section");
		log.info("INFO: click on entry under the section");						
		ui.clickLinkWait("css=span[id*='" + sectionID + "']");
		ui.clickLinkWait("css=a[href*='" + sectionID + "']:contains('Entry')");

		
		BaseActivityEntry entry = BaseActivityEntry
				.builder(testName + " entry" + Helper.genDateBasedRandVal())
				.tags(Helper.genDateBasedRandVal())
				.dateRandom()
				.customText(Data.getData().CustomFieldName,
						"Custom text for " + testName)
				.bookmark(
						Helper.genDateBasedRandVal() + "ActivitiesHome",
						driver.getCurrentUrl().replace("http://", "")
								+ "activities")						
				.description(Data.getData().commonDescription + testName)
				.addLinkToFile(linkToFile).addLinkToFolder(linkToFolder)
				.addPerson(communityMember1).markPrivate()
				.notifyMessage(Data.getData().commonComment).build();

		logger.strongStep("INFO: Create New entry for activity created above");
		log.info("INFO: Create New entry for activity created above");
		ui.fillEntry(entry);

		ui.clickLinkWithJavascript("css=a[href*='" + sectionID
				+ "']:contains('To Do Item')");

		BaseActivityToDo toDo = BaseActivityToDo
				.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
				.tags(testName + Helper.genDateBasedRandVal())
				.multipleAssignTo(communityOwner)
				.multipleAssignTo(communityMember1)
				// .addFile(Data.getData().file1)
				.customText(Data.getData().CustomFieldName,
						"Custom text for " + testName)
				.bookmark(
						Helper.genDateBasedRandVal() + "ActivitiesHome",
						driver.getCurrentUrl().replace("http://", "")
								+ "activities")
				.description(
						Data.getData().commonDescription
								+ Helper.genDateBasedRandVal()).dateRandom()
				.addLinkToFile(linkToFile).addLinkToFolder(linkToFolder)
				.addPerson(communityMember1).notifyPeople().notifyAllPeople()
				.notifyMessage(Data.getData().commonComment).build();

		logger.strongStep("INFO: Create To Do Item for activity created above");
		log.info("INFO: Create To Do Item for activity created above");
		ui.fillToDo(toDo);

		BaseActivityToDo todoRoot = BaseActivityToDo.builder(testName+ "ToDo" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		
		logger.strongStep("INFO: create To Do Section under root and click on cancel");
		log.info("INFO: create To Do Section under root and click on cancel");
		ui.createToDo(todoRoot);
		ui.clickCancelButton();
		
		String todoRootUUID = ui.getEntryUUID(todoRoot);
		ui.expandEntry(todoRootUUID);
		
		logger.strongStep("INFO: verify no add entry under the todo.");
		log.info("INFO: verify no add entry under the todo.");
		Boolean notPresent = ui.isTextNotPresentWithinElement(ui.getFirstVisibleElement("css=div[id='activityPageNodeContainer"+todoRootUUID+ "_node']"), "Add Entry");
        Assert.assertTrue(notPresent,"should not see Add Entry under a todo");

		logger.strongStep("INFO: create another todo under todo just created");
		log.info("INFO: create another todo under todo just created");        
		BaseActivityToDo todoUnderTodo = BaseActivityToDo.builder(testName+ "ToDo" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createTodoUnderEntry(todoRootUUID,todoUnderTodo);
		
		logger.strongStep("INFO: create an entry under root.");
		log.info("INFO: create an entry under root.");        		
		BaseActivityEntry entryRoot = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		
		logger.strongStep("INFO: create an activity for entry under root.");
		log.info("INFO: create an activity for entry under root."); 
		ui.createEntry(entryRoot);
		String entryRootUUID = ui.getEntryUUID(entryRoot);  
		ui.expandEntry(entryRootUUID);
		
		logger.strongStep("INFO: Verify that Add Entry text is not displayed under created entry");
		log.info("INFO: Verify that Add Entry text is not displayed under created entry"); 
		notPresent = ui.isTextNotPresentWithinElement(ui.getFirstVisibleElement("css=div[id='activityPageNodeContainer"+entryRootUUID+ "_node']"), "Add Entry");
        Assert.assertTrue(notPresent,"should not see Add Entry under an entry");
        
		logger.strongStep("INFO: create another todo under entry just created");
		log.info("INFO: create another todo under entry just created");        
		BaseActivityToDo todoUnderEntry = BaseActivityToDo.builder(testName+ "ToDo" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createTodoUnderEntry(entryRootUUID,todoUnderEntry);
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		logger.strongStep("INFO: Logout and Login to activity as "+ communityMember1);
		log.info("INFO: Logout and Login to activity as "+ communityMember1);
		ui.reloginCommunityActivity(communityMember1,activity1);		
		
		logger.strongStep("INFO: create a comment for a todo under root");
		log.info("INFO: create a comment for a todo under root");		
		ui.createCommentUnderEntry(todoRootUUID,Data.getData().commonComment);
		ui.collapseEntry(todoRootUUID);
		
		logger.strongStep("INFO: create a comment for a entry under root.");
		log.info("INFO: create a comment for a entry under root.");				
		ui.createCommentUnderEntry(entryRootUUID,Data.getData().commonComment);
		
		ui.endTest();

	}	

	/**
	 * <ul>
	 * <li><B>Info: Take basic actions in Activity</B></li>
	 * <li><B>Create an public community and add an activity with API</B></li>
	 * <li><B>login with the owner</B></li>
	 * <li><B>Set this activity to High priority </B></li>
	 * <li><B>goto High Priority view</B></li>
	 * <li><B>verify the activity can be found in the view</B></li>
	 * <li><B>open the activity, set the activity to Media priority</B></li>
	 * <li><B>goto Media Priority view,</B></li>
	 * <li><B>verify the activity can be found in the view</B></li>
	 * <li><B>open the activity, set the activity to Mark as Tuned Out</B></li>
	 * <li><B>goto Turned Out Activities view</B></li>
	 * <li><B>verify the activity can be found in the view</B></li>
	 * <li><B>goto Community Activities view</B></li>
	 * <li><B>verify the turned out activity cannot be found in community activities view</B></li>
	 * <li><B>open the activity, set the activity to Normal Priority</B></li>
	 * <li><B>goto Turned Out Activities view</B></li>
	 * <li><B>verify the activity cannot be found in the view.</B></li>
	 * <li><B>create entry in the activity</B></li>
	 * <li><B>copy the activity</B></li>
	 * <li><B>open the copy activity</B></li>
	 * <li><B>verify the same entry can be found in the copy activity</B></li>
	 * <li><B>go back to the original activity</B></li>
	 * <li><B>do copy activity, select anywhere</B></li>
	 * <li><B>add the copy activity as relative activity to this activity</B></li>
	 * <li><B>mark this activity complete</B></li>
	 * <li><B>goto Completed Activities view</B></li>
	 * <li><B>verify the activity can be found in the view</B></li>
	 * <li><B>open the activity</B></li>
	 * <li><B>click Restore Activity</B></li>
	 * <li><B>goto Completed Activities view</B></li>
	 * <li><B>verify the activity can not be found in the view</B></li>
	 * <li><B>login with another user </B></li>
	 * <li><B>open the activity</B></li>
	 * <li><B>Click Follow this Activity</B></li>
	 * <li><B>verify the user can follow the activity successfully</B></li>
	 * <li><B>click Stop Following this Activity</B></li>
	 * <li><B>verify the user can stop following the activity successfully</B></li>
	 * <li><B>as owner delete the activity</B></li>
	 * <li><B>verify the activity is deleted in trash</B></li>
	 * </ul>
	 */
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})	
	public void basicActionsInActivity(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		User communityOwner = cfg.getUserAllocator().getUser();
		User anotherUser = cfg.getUserAllocator().getUser();

		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, communityOwner.getAttribute(cfg.getLoginPreference()), communityOwner.getPassword());		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS,anotherUser))
													.shareOutside(false)
													.access(Access.RESTRICTED)
													.build();
		
		logger.strongStep("Create Community: " + community.getName() + " using API");
		log.info("INFO: Create wiki: " + community.getName() + " using API");
		Community comAPI = community.createAPI(comApiOwner);
		
		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		logger.strongStep("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName() + " using API");
		log.info("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);
    
		BaseActivity activity = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.build();
		
		logger.strongStep("INFO: Open browser and login to Activities as: " + communityOwner);
		log.info("INFO: Open browser and login to Activities as: " + communityOwner);
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(communityOwner);
		
		logger.strongStep("INFO: Create activity for created community");
		log.info("INFO: Create activity for created community");
		activity.createInCommunity(ui);

		logger.strongStep("INFO: Set this activity to High priority and navigate to activity high priority page");
		log.info("INFO: Set this activity to High priority and navigate to activity high priority page");		
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuHighPriority);
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallHighPriority);
		
		logger.strongStep("INFO: Verify that "+ activity.getName() +" is displayed in High Priority View");
		log.info("INFO: Verify that "+ activity.getName() +" is displayed in High Priority View");	
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()),"Error:cannot find activity:"+activity.getName()+" in High Priority View");

		logger.strongStep("INFO: Set this activity to Medium priority and navigate to activity Medium priority page");
		log.info("INFO: Set this activity to Medium priority and navigate to activity Medium priority page");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuMediumPriority);
		ui.gotoActivitiesMainPage();	
		ui.clickLinkWait(ActivitiesUIConstants.OverallMediumPriority);
		
		logger.strongStep("INFO: Verify that "+ activity.getName() +" is displayed in Medium Priority View");
		log.info("INFO: Verify that "+ activity.getName() +" is displayed in MEdium Priority View");	
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()),"Error:cannot find activity:"+activity.getName()+" in  Medium Priority View");

		logger.strongStep("INFO: Set this activity to Tuned Out and navigate to activity Turned Out page");
		log.info("INFO: Set this activity to Tuned Out and navigate to activity Turned Out page");		
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuTurnedOut);
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallTurnedOut);
		
		logger.strongStep("INFO: Verify that "+ activity.getName() +" is displayed in Turned Out View");
		log.info("INFO: Verify that "+ activity.getName() +" is displayed in Turned Out View");
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()),"Error:cannot find activity:"+activity.getName()+" in Turned Out View");	
		
		logger.strongStep("INFO: Navigate to Community Activities and verify that created turned out activity is not displayed");
		log.info("INFO: Navigate to Community Activities and verify that created turned out activity is not displayed");
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(activity.getName()),"Error:should not find turned out activity:"+activity.getName()+" in My Activities View");			

		logger.strongStep("INFO: Set this activity to Normal priority and navigate to activity turned out page");
		log.info("INFO: Set this activity to Normal priority and navigate to activity turned out page");
		ui.clickLinkWait(ActivitiesUIConstants.OverallTurnedOut);
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuNormalPriority);
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallTurnedOut);
		
		logger.strongStep("INFO: Verify that created normal activity is not shown in turned out view");
		log.info("INFO: Verify that created normal activity is not shown in turned out view");
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(activity.getName()),"Error:should not find normal activity:"+activity.getName()+" in Turned Out View");		
	
		logger.strongStep("INFO: Create an entry inside Activity");
		log.info("INFO: Create an entry inside Activity");
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry);

		logger.strongStep("INFO: Create a copy of Activity from original activity");
		log.info("INFO: Create a copy of Activity from original activity");
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuCopyActivity);
		ui.fluentWaitTextPresent("Copy \""+activity.getName()+"\""+" into New Activity");
		String copyActivityName = testName + Helper.genDateBasedRand();
		ui.clearText("css=input[id^=lconn_act_ActivityForm_][id$=titleInput]");
		ui.typeText("css=input[id^=lconn_act_ActivityForm_][id$=titleInput]", copyActivityName);
		ui.clickSaveButton();
		Assert.assertTrue(ui.fluentWaitTextPresent(entry.getTitle()),"Error: Cannot find the entry from the orginal activity in the copy activity");
		
		logger.strongStep("INFO: Add the copy activity as relative activity to the original activity");
		log.info("INFO: Add the copy activity as relative activity to the original activity");
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuAddRelatedActivity);
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().AddRelatedActivity), 
		"ERROR: The dialog could not be found");
		log.info("INFO: Select the related activity from the dialog");
		ui.clickLinkWait("css=a[aria-label='Show activities from: Everywhere']");	
		ui.clickLinkWait("css=label:contains('" + copyActivityName + "')");
		ui.clickSaveButton();

		String selector = "css=div[class='entryIcon nodeTypeEntry lconnSprite lconnSprite-iconActivities16'] " +
			" + h4[id$='_miniTitle'] > span:contains('" + copyActivityName + "')";
		
		logger.strongStep("INFO: Verify the related activity displays as an entry in the outline view");
		log.info("INFO: Verify the related activity displays as an entry in the outline view");
		Assert.assertTrue(driver.isElementPresent(selector), "ERROR: Related Activity " + copyActivityName + " could not be found");
		
		logger.strongStep("INFO: Select the entry");
		log.info("INFO: Select the entry");
		ui.clickLink(selector);
		
		selector = "css=a[class='lotusBookmarkField']:contains('" + copyActivityName + "')";
		
		logger.strongStep("INFO: Verify the related activity displays as a bookmark within the entry");
		log.info("INFO: Verify the related activity displays as a bookmark within the entry");
		Assert.assertTrue(driver.isElementPresent(selector), "ERROR: " + copyActivityName + " bookmark could not be found");		
		
		logger.strongStep("INFO: Mark this activity as complete and navigate to overall completd activities page");
		log.info("INFO: Mark this activity as complete and navigate to overall completd activities page");		
		ui.clickLinkWait("css=a:contains(Mark Activity Complete)");
		ui.gotoActivitiesMainPage();		
		ui.clickLinkWait(ActivitiesUIConstants.OverallCompletedActivities);
		
		logger.strongStep("INFO: Verify the created overall complete activity is displayed");
		log.info("INFO: Verify the created overall complete activity is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()),"Error:cannot find activity:"+activity.getName()+" in Completed Activities View");
		
		logger.strongStep("INFO: Mark this activity as restore and navigate to overall completd activities page");
		log.info("INFO: Mark this activity as restore and navigate to overall completd activities page");		
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		ui.clickLinkWait("css=a:contains(Restore Activity)");
		ui.gotoActivitiesMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallCompletedActivities);
		
		logger.strongStep("INFO: Verify the created overall restore activity is displayed in Overall Complete Activities page");
		log.info("INFO: Verify the created overall restore activity is displayed in Overall Complete Activities page");
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(activity.getName()),"Error:should not find activity:"+activity.getName()+" in Completed Activities View");
		
		logger.strongStep("INFO: Logout, login with another user, follow the activity and verify that unfollow link is displayed");
		log.info("INFO: Logout, login with another user, follow the activity and verify that unfollow link is displayed");			
		ui.reloginCommunityActivity(anotherUser, activity);
		ui.clickLinkWait(ActivitiesUIConstants.CommunityFolowMenu);
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivityFollow);
		ui.clickLinkWait(ActivitiesUIConstants.CommunityFolowMenu);
		Element stopFollowingLink = driver.getSingleElement(ActivitiesUIConstants.CommunityActivityUnFollow);
		Assert.assertTrue(stopFollowingLink != null, "Attempted to follow an activity, but the " +
				"option to unfollow did not appear.");
		
		logger.strongStep("INFO: Unfollow the activity and verify that follow link is displayed");
		log.info("INFO: Unfollow the activity and verify that follow link is displayed");	
		stopFollowingLink.click();
		ui.clickLinkWait(ActivitiesUIConstants.CommunityFolowMenu);
		Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.CommunityActivityFollow),
				"Attempted to unfollow an activity, but the " +
				"option to follow did not appear.");
		
		logger.strongStep("INFO: Logout, login as community owner and delete the activity");
		log.info("INFO: Logout, login as community owner and delete the activity");	
		ui.reloginCommunityActivity(communityOwner,activity);
		ui.clickLink(ActivitiesUIConstants.activityActionMenu);
		ui.clickLink(ActivitiesUIConstants.actionMenuDeleteActivity);
		ui.clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);

		logger.strongStep("INFO: Go to trash folder and verify deleted activity is present");
		log.info("INFO: Go to trash folder and verify deleted activity is present");			
		ui.gotoActivitiesMainPage();	
		ui.clickLink(ActivitiesUIConstants.OverallTrash);
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()),
				"Activity not visible in trash: " + activity.getName());

		logger.strongStep("INFO: Restore the activity from trash folder and verify the activity is displayed");
		log.info("INFO: Restore the activity from trash folder and verify the activity is displayed");
		ui.clickLink("css=tr[uuid='" + ui.getActivityUUID(activity) + "'] td.lotusLastCell a:contains('More')");
		ui.clickLinkWait("css=tr[id$='" + ui.getActivityUUID(activity) + "detailsRow'] a:contains('Restore')");
		ui.clickLink(ActivitiesUIConstants.OverallCommunityActivities);

		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()),"Error:cannot find activity:"+activity.getName()+" in Community Activities View");
		
		ui.endTest();
	}	
	
	/**
	 * <ul>
	 * <li><B>Info: Take basic actions in Entry/Todo</B></li>
	 * <li><B>create a community and add an author</B></li>
	 * <li><B>create an activity as the owner, add community member as author</B></li>
	 * <li><B>as the author create a entry and a todo</B></li>
	 * <li><B>for the entry do:</B></li>
	 * <li><B>Notify other people (select all)</B></li>
	 * <li><B>Link to this entry</B></li>
	 * <li><B>verify: the entry is in the new page but the todo isnot</B></li>
	 * <li><B>click activity outline</B></li>
	 * <li><B>create another activity in the community</B></li>
	 * <li><B>copy the entry to the new activity</B></li>
	 * <li><B>verify no copy to entry tab for entry</B></li>
	 * <li><B>verify the new activity is opened</B></li>
	 * <li><B>verify the entry in the new activity</B></li>
	 * <li><B>go back to the first activity</B></li>
	 * <li><B>create the second entry</B></li>
	 * <li><B>move the new entry to the second activity</B></li>
	 * <li><B>verify Move to Entry/To Do tab for entry</B></li>
	 * <li><B>verify the new activity is opened</B></li>
	 * <li><B>verify the entry in the new activity</B></li>
	 * <li><B>go back to the first activity</B></li>
	 * <li><B>verify the entry is no longer in the first activity</B></li>
	 * <li><B>create another entry</B></li>
	 * <li><B>convert entry to todo</B></li>
	 * <li><B>verify the entry becomes to todo</B></li>
	 * <li><B>delete the first entry</B></li>
	 * <li><B>verify delete is successful</B></li>
	 * </ul>
	 */	
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})	
	public void basicActionsInEntry(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		User communityOwner = cfg.getUserAllocator().getUser();
		User communityMember;
		BaseCommunity community;
		if(cfg.getProductName().toLowerCase().equals("cloud")){
			communityMember = cfg.getUserAllocator().getGroupUser("external_group");
			CommunitiesUI comui = CommunitiesUI.getGui(cfg.getProductName(), driver);
			
			community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
														.addexMember(new Member(CommunityRole.MEMBERS,communityMember))
														.access(Access.RESTRICTED)
														.build();
			
			logger.strongStep("INFO: Open browser and login to Community as: " + communityOwner);
			log.info("INFO: Open browser and login to Community as: " + communityOwner);
			comui.loadComponent(Data.getData().ComponentCommunities);		
			comui.login(communityOwner);
			
			logger.strongStep("INFO: Create a community");
			log.info("INFO: Create a community");
			community.create(comui);
			
			logger.strongStep("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName());
			log.info("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName());
			community.addWidget(comui, BaseWidget.ACTIVITIES);

		}else{

			communityMember = cfg.getUserAllocator().getUser();
			APICommunitiesHandler comApiOwner;
			comApiOwner = new APICommunitiesHandler(serverURL, communityOwner.getAttribute(cfg.getLoginPreference()), communityOwner.getPassword());		
			community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
														.addMember(new Member(CommunityRole.MEMBERS,communityMember))
														.access(Access.RESTRICTED)
														.shareOutside(false)
														.build();
			
			logger.strongStep("INFO: Create community using API");
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(comApiOwner);

			community.getCommunityUUID_API(comApiOwner, comAPI);
			
			logger.strongStep("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName() + " using API");
			log.info("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName() + " using API");
			community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);

			logger.strongStep("INFO: Open browser and login to Community Activity as: " + communityOwner);
			log.info("INFO: Open browser and login to Community Activity as: " + communityOwner);
			ui.loadComponent(Data.getData().ComponentActivities);
			ui.login(communityOwner);			
		}
		
		
		logger.strongStep("INFO: Create an activity1 for community");
		log.info("INFO: Create an activity1 for community");
		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();
		activity1.createInCommunity(ui);
		
		logger.strongStep("INFO: Logout, Login to community Activity1 page as :" + communityMember);
		log.info("INFO: Logout, Login to community Activity1 page as :" + communityMember);
		ui.reloginCommunityActivity(communityMember, activity1);	

		logger.strongStep("INFO: Create an entry,click on add  todo for activity1 and cancel the todo");
		log.info("INFO: Create an entry,click on add  todo for activity1 and cancel the todo");		
		BaseActivityEntry entry1 = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry1);

		BaseActivityToDo todo1 = BaseActivityToDo.builder(testName+ "ToDo" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createToDo(todo1);
		ui.clickCancelButton();			
		
		logger.strongStep("INFO: Notify all on the entry");
		log.info("INFO: Notify all on the entry");		
		String entry1UUID = ui.getEntryUUID(entry1);
		ui.moreActionsForEntry(entry1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionNotify);
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry_Notify_Checkbox_NotifyAll);
		ui.clickLinkWait("css=input[type='button'][value='Send']");
		ui.clickLinkWait("css=input[value='OK']");
		
		logger.strongStep("INFO: link to entry");
		log.info("INFO: link to entry");
		ui.moreActionsForEntry(entry1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionLinkToEntry);
		
		logger.strongStep("INFO: Verify that newly created entry is displayed and to do is not displayed");
		log.info("INFO: Verify that newly created entry is displayed and to do is not displayed");	
		Assert.assertTrue(ui.fluentWaitTextPresent(entry1.getTitle()),"Error: entry "+ entry1.getTitle()+" doesn't exist");
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo1.getTitle()),"Error: should not find todo "+todo1.getTitle());

		logger.strongStep("INFO: create another activity with api");
		log.info("INFO: create another activity with api");
		BaseActivity activity2 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
		.community(community)
		.implicitRole(ActivityRole.AUTHOR)
		.build();
		
		logger.strongStep("INFO: Create activity2 for created community");
		log.info("INFO: Create activity2 for created community");
		activity2.createInCommunity(ui,CommunityRole.MEMBERS);
		
		logger.strongStep("INFO: copy the entry to the Activity1 to Activity2");
		log.info("INFO: copy the entry to the Activity1 to Activity2");		
		ui.openCommunityActivity(activity1);
		ui.moreActionsForEntry(entry1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionCopy);
		ui.getFirstVisibleElement("css=label:contains('" + activity2.getName() + "')").click();
		ui.clickLinkWait("css=input[value='Copy']");
		
		logger.strongStep("INFO: Verify the entry copied from Activity1 to Activity2");
		log.info("INFO: Verify the entry copied from Activity1 to Activity2");	
        Assert.assertTrue(ui.fluentWaitTextPresent(entry1.getTitle()),"Error: doesn't find the entry in the copy to activity");

		logger.strongStep("INFO: Create new entry in Activity1 and move it from Activity1 to Activity2");
		log.info("INFO: Create new entry in Activity1 and move it from Activity1 to Activity2");        
		ui.openCommunityActivity(activity1);
		BaseActivityEntry entry2 = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry2);
		String entry2UUID = ui.getEntryUUID(entry2);
		ui.moreActionsForEntry(entry2UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionMove);
		ui.getFirstVisibleElement("css=label:contains('" + activity2.getName() + "')").click();		
		ui.clickLinkWait("css=input[value='Move']");
		
		logger.strongStep("INFO: Verify that new entry is displayed in Activity2");
		log.info("INFO: Verify that new entry is displayed in Activity2"); 
        Assert.assertTrue(ui.fluentWaitTextPresent(entry2.getTitle()),"Error: doesn't find the entry in the move to activity");	
        
        logger.strongStep("INFO: Verify that new entry is not displayed in Activity1");
		log.info("INFO: Verify that new entry is not displayed in Activity1"); 
		ui.openCommunityActivity(activity1);
        Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(entry2.getTitle()),"Error: shouldn't find the entry in the move from activity");				
      
        logger.strongStep("INFO: Create another entry and convert entry to todo");
		log.info("INFO: Create another entry and convert entry to todo");        
		BaseActivityEntry entry3 = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry3);
		String entry3UUID = ui.getEntryUUID(entry3);
		ui.moreActionsForEntry(entry3UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionConvert);
		ui.clickSaveButton();
		
		logger.strongStep("INFO: Verify that entry is converted to todo");
		log.info("INFO: Verify that entry is converted to todo");    
		Assert.assertTrue(ui.fluentWaitPresent("id=activityPageNodeContainer"+entry3UUID+"_checkBox"),"the entry isn't convernt to todo");
		
		logger.strongStep("INFO: Delete the entry");
		log.info("INFO: Delete the entry");        		
		ui.moreActionsForEntry(entry1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionDelete);
		ui.clickLinkWait("css=input[value='OK']");
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(entry1.getTitle()),"the deleted is still there");	
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: Take basic actions in Entry/Todo</B></li>
	 * <li><B>create a community and add an author</B></li>
	 * <li><B>create an activity as the owner, add community member as author</B></li>
	 * <li><B>as the author create a entry and a todo</B></li>
	 * <li><B>for the todo do:</B></li>
	 * <li><B>Notify other people (select all)</B></li>
	 * <li><B>Link to this todo</B></li>
	 * <li><B>verify: the todo is in the new page but the entry isnot</B></li>
	 * <li><B>click activity outline</B></li>
	 * <li><B>copy the todo to the new activity</B></li>
	 * <li><B>verify has COPY TO ENTRY tab for entry</B></li>
	 * <li><B>verify the new activity is opened</B></li>
	 * <li><B>verify the todo in the new activity</B></li>
	 * <li><B>go back to the first activity</B></li>
	 * <li><B>create the second todo</B></li>
	 * <li><B>move the new todo to the second activity</B></li>
	 * <li><B>verify has Move to Entry/To Do tab for todo</B></li>
	 * <li><B>verify the new activity is opened</B></li>
	 * <li><B>verify the todo is in the new activity</B></li>
	 * <li><B>go back to the first activity</B></li>
	 * <li><B>verify the todo is no longer in the first activity</B></li>
	 * <li><B>delete the first todo</B></li>
	 * <li><B>verify delete is successful</B></li>
	 * </ul>
	 */		
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})	
	public void basicActionsInTodo(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		User communityOwner = cfg.getUserAllocator().getUser();
		User communityMember;
		BaseCommunity community;
		if(cfg.getProductName().toLowerCase().equals("cloud")){
			communityMember = cfg.getUserAllocator().getGroupUser("external_group");
			CommunitiesUI comui = CommunitiesUI.getGui(cfg.getProductName(), driver);
			
			community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
														.addexMember(new Member(CommunityRole.MEMBERS,communityMember))
														.access(Access.RESTRICTED)
														.build();
			
			logger.strongStep("INFO: Open browser and login to Community as: " + communityOwner);
			log.info("INFO: Open browser and login to Community as: " + communityOwner);
			comui.loadComponent(Data.getData().ComponentCommunities);		
			comui.login(communityOwner);
			
			logger.strongStep("INFO: Create a community");
			log.info("INFO: Create a community");
			community.create(comui);
			
			logger.strongStep("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName());
			log.info("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName());
			community.addWidget(comui, BaseWidget.ACTIVITIES);

		}else{

			communityMember = cfg.getUserAllocator().getUser();
			APICommunitiesHandler comApiOwner;
			comApiOwner = new APICommunitiesHandler(serverURL, communityOwner.getAttribute(cfg.getLoginPreference()), communityOwner.getPassword());		
			community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
														.addMember(new Member(CommunityRole.MEMBERS,communityMember))
														.access(Access.RESTRICTED)
														.shareOutside(false)
														.build();
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(comApiOwner);

			community.getCommunityUUID_API(comApiOwner, comAPI);
			
			community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);
			ui.loadComponent(Data.getData().ComponentActivities);

			ui.login(communityOwner);			
		}		
		
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.implicitRole(ActivityRole.AUTHOR)
				.build();
		activity1.createInCommunity(ui);

		logger.strongStep("INFO: Logout, Login to community Activity1 page as :" + communityMember);
		log.info("INFO: Logout, Login to community Activity1 page as :" + communityMember);
		ui.reloginCommunityActivity(communityMember, activity1);	
		
		logger.strongStep("INFO: Create an entry,click on add  todo for activity1 and cancel the todo");
		log.info("INFO: Create an entry,click on add  todo for activity1 and cancel the todo");	
		BaseActivityEntry entry1 = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		ui.createEntry(entry1);

		BaseActivityToDo todo1 = BaseActivityToDo.builder(testName+ "ToDo" + Helper.genDateBasedRandVal())
		.tags(testName + Helper.genDateBasedRandVal())
		.build();
		ui.createToDo(todo1);
		ui.clickCancelButton();	
		
		logger.strongStep("INFO: Notify all on the entry");
		log.info("INFO: Notify all on the entry");
		String todo1UUID = ui.getEntryUUID(todo1);
		ui.moreActionsForEntry(todo1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionNotify);
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry_Notify_Checkbox_NotifyAll);
		ui.clickLinkWait("css=input[type='button'][value='Send']");
		ui.clickLinkWait("css=input[value='OK']");
		
		logger.strongStep("INFO: Link to entry");
		log.info("INFO: Link to entry");		
		ui.moreActionsForEntry(todo1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionLinkToEntry);

		logger.strongStep("INFO: Verify that newly created entry is displayed and to do is not displayed");
		log.info("INFO: Verify that newly created entry is displayed and to do is not displayed");	
		Assert.assertTrue(ui.fluentWaitTextPresent(todo1.getTitle()),"Error: todo "+ todo1.getTitle()+" doesn't exist");
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(entry1.getTitle()),"Error: should not find entry "+entry1.getTitle());

		logger.strongStep("INFO: create another Activity2 in same community");
		log.info("INFO: create another Activity2 in same community");
		BaseActivity activity2 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
		.community(community)
		.implicitRole(ActivityRole.AUTHOR)
		.build();
		activity2.createInCommunity(ui,CommunityRole.MEMBERS);
		
		logger.strongStep("INFO: Copy todo to Activity1 to Activity2");
		log.info("INFO: Copy todo to Activity1 to Activity2");
		ui.openCommunityActivity(activity1);		
		ui.moreActionsForEntry(todo1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionCopy);
		ui.getFirstVisibleElement("css=label:contains('" + activity2.getName() + "')").click();
		ui.clickLinkWait("css=input[value='Copy']");
		
		logger.strongStep("INFO: Verify the toDo copied from Activity1 to Activity2");
		log.info("INFO: Verify the entry toDo from Activity1 to Activity2");	
        Assert.assertTrue(ui.fluentWaitTextPresent(todo1.getTitle()),"Error: doesn't find the entry in the copy to activity");

        logger.strongStep("INFO: Create new toDo in Activity1 and move it from Activity1 to Activity2");
		log.info("INFO: Create new toDo in Activity1 and move it from Activity1 to Activity2"); 
		ui.openCommunityActivity(activity1);
		BaseActivityToDo todo2 = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		ui.createToDo(todo2);
		ui.clickCancelButton();			
		String todo2UUID = ui.getEntryUUID(todo2);
		ui.moreActionsForEntry(todo2UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionMove);
		ui.getFirstVisibleElement("css=label:contains('" + activity2.getName() + "')").click();		
		ui.clickLinkWait("css=input[value='Move']");
		
		logger.strongStep("INFO: Verify that new toDo is displayed in Activity2");
		log.info("INFO: Verify that new toDo is displayed in Activity2"); 
        Assert.assertTrue(ui.fluentWaitTextPresent(todo2.getTitle()),"Error: doesn't find the todo in the move to activity");
        
        logger.strongStep("INFO: Verify that new toDo is not displayed in Activity1");
		log.info("INFO: Verify that new toDo is not displayed in Activity1"); 
		ui.openCommunityActivity(activity1);
        Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo2.getTitle()),"Error: shouldn't find the todo in the move from activity");				

        logger.strongStep("INFO: delete toDo");
		log.info("INFO: delete toDo");
		ui.moreActionsForEntry(todo1UUID);
		ui.clickLinkWait(ActivitiesUIConstants.actionDelete);
		ui.clickLinkWait("css=input[value='OK']");
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo1.getTitle()),"the deleted is still there");	
		ui.endTest();
		
	}

	/**
	 * <ul>
	 * <li><B>Info: test basic functions of multiple todo</B></li>
	 * <li><B>create a community and add two members</B></li>
	 * <li><B>create an activity with owner and add all community as the author</B></li>
	 * <li><B>user author to create a todo and assign to all members by using select all</B></li>
	 * <li><B>verify: all members are added as the assignees of the todo</B></li>
	 * <li><B>use reader to complete the todo by using the individual checkbox</B></li>
	 * <li><B>verify: the overall checkbox is checked</B></li>
	 * <li><B>verify: individual checkbox is checked</B></li>
	 * <li><B>verify: the overall status is not completed</B></li>
	 * <li><B>uncheck the individual checkbox</B></li>
	 * <li><B>verify: the overall checkbox is unchecked</B></li>
	 * <li><B>verify: individual checkbox is unchecked</B></li>
	 * <li><B>still use the reader to complete the todo by using the overall checkbox</B></li>
	 * <li><B>verify: the overall checkbox is checked</B></li>
	 * <li><B>verify: individual checkbox is checked</B></li>
	 * <li><B>verify: the overall status is not completed</B></li>
	 * <li><B>check in TO DO LIST->Incomplete To Do</B></li>
	 * <li><B>verify: the todo is in the view</B></li>
	 * <li><B>check in TO DO LIST->Completed To Do Items</B></li>
	 * <li><B>verify: the todo is not in the view</B></li>
	 * <li><B>login with creator</B></li>
	 * <li><B>complete the todo by using the overall checkbox</B></li>
	 * <li><B>verify: force complete dialog popup</B></li>
	 * <li><B>click cancel</B></li>
	 * <li><B>complete the todo by using the individual checkbox</B></li>
	 * <li><B>verify: the individual checkbox is checked</B></li>
	 * <li><B>verify: the overall checkbox is not checked.</B></li>
	 * <li><B>check in TO DO LIST->Incomplete To Do</B></li>
	 * <li><B>verify: the todo is in the view</B></li>
	 * <li><B>check in TO DO LIST->Completed To Do Items</B></li>
	 * <li><B>verify: the todo is not in the view</B></li>
	 * <li><B>login with the activity owner(as the third assignee)</B></li>
	 * <li><B>complete the todo by using the individual checkbox</B></li>
	 * <li><B>verify: the overall checkbox is checked</B></li>
	 * <li><B>verify: individual checkbox is checked</B></li>
	 * <li><B>verify: the overall status is completed</B></li>
	 * <li><B>check in TO DO LIST->Incomplete To Do</B></li>
	 * <li><B>verify: the todo is not in the view</B></li>
	 * <li><B>check in TO DO LIST->Completed To Do Items</B></li>
	 * <li><B>verify: the todo is in the view</B></li>
	 * <li><B>login with reader</B></li>
	 * <li><B>check in TO DO LIST->Incomplete To Do</B></li>
	 * <li><B>verify: the todo is not in the view</B></li>
	 * <li><B>check in TO DO LIST->Completed To Do Items</B></li>
	 * <li><B>verify: the todo is in the view</B></li>
	 * <li><B>uncheck the todo by using the individual checkbox</B></li>
	 * <li><B>verify: the overall checkbox is unchecked</B></li>
	 * <li><B>verify: individual checkbox is unchecked</B></li>
	 * <li><B>verify: the overall status is not completed</B></li>
	 * <li><B>complete the todo by using the overall checkbox</B></li>
	 * <li><B>verify: the overall checkbox is checked</B></li>
	 * <li><B>verify: individual checkbox is checked</B></li>
	 * <li><B>verify: the overall status is completed</B></li>
	 * <li><B>uncheck the todo by using the overall checkbox</B></li>
	 * <li><B>verify: the overall checkbox is unchecked</B></li>
	 * <li><B>verify: individual checkbox is unchecked</B></li>
	 * <li><B>verify: the overall status is not completed</B></li>
	 * </ul>
	 */
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})		
	public void multipleTodoBasic(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User internalUser1 = cfg.getUserAllocator().getUser();
		User internalUser2 = cfg.getUserAllocator().getUser();
		User internalUser3 = cfg.getUserAllocator().getUser();
		
		String testName = ui.startTest();
		ActivityMember author = new ActivityMember(ActivityRole.AUTHOR, internalUser2, ActivityMember.MemberType.PERSON);		
		ActivityMember reader = new ActivityMember(ActivityRole.READER, internalUser3, ActivityMember.MemberType.PERSON);
	
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, internalUser1.getAttribute(cfg.getLoginPreference()), internalUser1.getPassword());		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS,author.getUser()))
													.addMember(new Member(CommunityRole.MEMBERS,reader.getUser()))													
													.shareOutside(false)
													.access(Access.RESTRICTED)
													.build();
		
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		logger.strongStep("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName() + " using API");
		log.info("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() +" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);				
		
		BaseActivity activity = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.addMember(author)
				.build();
		
		logger.strongStep("INFO: Open browser and login to Activity as: " + internalUser1);
		log.info("INFO: Open browser and login to Activity as: " + internalUser1);	
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(internalUser1);

		logger.strongStep("INFO: Create an activity for community and add member from same community");
		log.info("INFO: Create an activity for community and add member from same community");
		activity.createInCommunity(ui);
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		ui.clickLinkWait(ActivitiesUIConstants.AddMembersButton);
		ui.addMemberFromCommunity(reader);
		ui.clickSaveButton();
		ui.fluentWaitPresent(ActivitiesUIConstants.memberHasReaderRole);

		logger.strongStep("INFO: Logout, Login as" + author.getUser()+ " and create a todo, add all members as assignees");
		log.info("INFO: Logout, Login as" + author.getUser()+ " and create a todo, add all members as assignees");
		ui.reloginCommunityActivity(author.getUser(), activity);

		BaseActivityToDo todo = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.build();
		ui.createToDo(todo);
		ui.clickCancelButton();	
		String todoUUID = ui.getEntryUUID(todo);
		ui.editEntryLink(todoUUID);
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		ui.getFirstVisibleElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown).useAsDropdown().selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));
		driver.getSingleElement("css=input[id=selectAllAssignees]").click();
		ui.clickSaveButton();
		Element todoDiv = driver.getSingleElement("css=div[id='activityPageNodeContainer"+todoUUID+"_node']");
		
		logger.strongStep("INFO: Verify that all assigned members are display in created toDo Section");
		log.info("INFO: Verify that all assigned members are display in created toDo Section");
		Assert.assertTrue(
				ui.isTextPresentWithinElement(todoDiv,
						internalUser1.getDisplayName())
						&& ui.isTextPresentWithinElement(todoDiv, author
								.getUser().getDisplayName())
						&& ui.isTextPresentWithinElement(todoDiv, reader
								.getUser().getDisplayName()),
				"at least one assignee is not found");
		
		String ownerUserID = getUserIDFromAssignee(todoUUID, internalUser1.getDisplayName());
		String authorUserID = getUserIDFromAssignee(todoUUID, author.getUser().getDisplayName());
		String readerUserID = getUserIDFromAssignee(todoUUID, reader.getUser().getDisplayName());		

		logger.strongStep("INFO: Logout, Login to community Activity  as :" + reader.getUser());
		log.info("INFO: Logout, Login to community Activity  as :" + reader.getUser());
		ui.reloginCommunityActivity(reader.getUser(), activity);
		
		logger.strongStep("INFO: Check the checkbox for "+ readerUserID);
		log.info("INFO: Check the checkbox for "+ readerUserID);
		ui.checkIndividualCheckBox(todoUUID, readerUserID);
		
		logger.strongStep("INFO: Verify that overall checkboxes are not checked and " + readerUserID +"checkbox is checked");
		log.info("INFO: Verify that overall checkboxes are not checked and " + readerUserID +"checkbox is checked");
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todoUUID),"Error:the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, readerUserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(!ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status should not be completed");

		logger.strongStep("INFO: Uncheck the checkbox for "+ readerUserID);
		log.info("INFO: Uncheck the checkbox for "+ readerUserID);
		ui.uncheckIndividualCheckBox(todoUUID, readerUserID);
		
		logger.strongStep("INFO: Verify that overall checkboxes are unchecked");
		log.info("INFO: Verify that overall checkboxes are not unchecked");
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todoUUID),"Error:the overall checkbox should not be checked");
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todoUUID, readerUserID),"Error: the individual checkbox should not be checked");

		logger.strongStep("INFO: still use the reader to complete the todo by using the overall checkbox");
		log.info("INFO: still use the reader to complete the todo by using the overall checkbox");		
		ui.checkOverallCheckBox(todoUUID); 
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todoUUID),"Error:the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, readerUserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(!ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status should not be completed");
		ui.gotoToDoListMainPage();		

		logger.strongStep("INFO: Verify that toDo created are displayed under Overall Incompleted section");
		log.info("INFO: Verify that toDo created are displayed under Overall Incompleted section");	
		ui.clickLinkWait(ActivitiesUIConstants.OverallInCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextPresent(todo.getTitle()));
		
		logger.strongStep("INFO: Verify that toDo created are displayed under Overall Completed section");
		log.info("INFO: Verify that toDo created are displayed under Overall Completed section");
		ui.clickLinkWait(ActivitiesUIConstants.OverallCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo.getTitle()));
		
		logger.strongStep("INFO: login with creator, complete the todo by using the overall checkbox");
		log.info("INFO: login with creator, complete the todo by using the overall checkbox");				
		ui.reloginCommunityActivity(author.getUser(), activity);
		
		ui.checkOverallCheckBox(todoUUID);
		Assert.assertTrue(ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?"),"Error: force complete dialog doesn't popup");
		ui.clickCancelButton();
		
		logger.strongStep("INFO: complete the todo by using the individual checkbox");
		log.info("INFO: complete the todo by using the individual checkbox");		
		ui.checkIndividualCheckBox(todoUUID, authorUserID);
		
		logger.strongStep("INFO: Verify that individual checkbox is checked an overall checkbox is unchecked");
		log.info("INFO: Verify that individual checkbox is checked an overall checkbox is unchecked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, authorUserID),"Error: the individual checkbox isn't checked");
		Assert.assertFalse(ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox should not be checked");
		
		
		
		logger.strongStep("INFO: Verify that the created toDo is not displayed in Overall Incompleted ToDo section");
		log.info("INFO: Verify that the created toDo is not displayed in Overall Incompleted ToDo section");
		ui.gotoToDoListMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallInCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextPresent(todo.getTitle()));
		
		logger.strongStep("INFO: Verify that the created toDo is  displayed in Overall Completed ToDo section");
		log.info("INFO: Verify that the created toDo is displayed in Overall completed ToDo section");
		ui.clickLinkWait(ActivitiesUIConstants.OverallCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo.getTitle()));	

		log.info("INFO: Logout, Login with the activity owner(as the third assignee), complete the todo by using the individual checkbox");	
		ui.reloginCommunityActivity(internalUser1, activity);
		
		ui.checkIndividualCheckBox(todoUUID, ownerUserID);
		
		logger.strongStep("INFO: Verify that all individual checkboxes and overall checkbox is checked");
		log.info("INFO: Verify that all individual checkboxes and overall checkbox is checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, ownerUserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox isn't checked");		
		Assert.assertTrue(ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status isn't completed");
		
		ui.gotoToDoListMainPage();
		
		logger.strongStep("INFO: Verify that the created toDo is  not displayed in Overall Incompleted ToDo section");
		log.info("INFO: Verify that the created toDo is not displayed in Overall Incompleted ToDo section");
		ui.clickLinkWait(ActivitiesUIConstants.OverallInCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo.getTitle()));
		
		logger.strongStep("INFO: Verify that the created toDo is displayed in Overall Completed ToDo section");
		log.info("INFO: Verify that the created toDo is displayed in Overall Completed ToDo section");
		ui.clickLinkWait(ActivitiesUIConstants.OverallCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextPresent(todo.getTitle()));
		
		logger.strongStep("INFO: Logout, Login again as reader");
		log.info("INFO: Logout, Login again as reader");			
		ui.reloginActivities(reader.getUser());
		ui.waitForPageLoaded(driver);
/*	defect_blocking	
 *  remark the steps for a bug that overall checkbox status cannot synch to individual if the todo has ever been shown in todo list view. 
		ui.clickLinkWait(ActivitiesUI.ToDoListTab);
		ui.clickLinkWait(ActivitiesUI.OverallInCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo.getTitle()));
		ui.clickLinkWait(ActivitiesUI.OverallCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextPresent(todo.getTitle()));*/
		
		logger.strongStep("INFO: uncheck the todo by using the individual checkbox");
		log.info("INFO: uncheck the todo by using the individual checkbox");		
		ui.openCommunityActivity(activity);
		ui.uncheckIndividualCheckBox(todoUUID, readerUserID);
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todoUUID, readerUserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox should not be checked");		
		Assert.assertTrue(!ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status should not be completed");		

		logger.strongStep("INFO: check the todo by using the individual checkbox and overall checkbox");
		log.info("INFO: check the todo by using the individual checkbox and overall checkbox");		
		ui.checkOverallCheckBox(todoUUID);
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, readerUserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox isn't checked");		
		Assert.assertTrue(ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status isn't completed");
		
		logger.strongStep("INFO: Uncheck the todo by unchecking the individual checkbox and overall checkbox");
		log.info("INFO: Uncheck the todo by unchecking the individual checkbox and overall checkbox");			
		ui.uncheckOverallCheckBox(todoUUID);
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todoUUID, readerUserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox should not be checked");		
		Assert.assertTrue(!ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status should not be completed");				
		ui.endTest();		
		
	}	
	
	/**
	 * <ul>
	 * <li><B>Info: multiple todo owner/creator force complete in regular
	 * activity</B></li>
	 * <li><B>create a community and add two members</B></li>
	 * <li><B>create an activity with owner and add all community as the
	 * author</B></li>
	 * <li><B>create a todo with author, add all members as assignees</B></li>
	 * <li><B>login with the other author</B></li>
	 * <li><B>complete the todo by using the overall checkbox</B></li>
	 * <li><B>verify: no force complete dialog popup</B></li>
	 * <li><B>verify: the overall checkbox is checked</B></li>
	 * <li><B>verify: individual checkbox is checked</B></li>
	 * <li><B>verify: the overall status is not completed</B></li>
	 * <li><B>login with the creator</B></li>
	 * <li><B>complete the todo by using the overall checkbox</B></li>
	 * <li><B>verify: the force complete dialog popup</B></li>
	 * <li><B>click ok</B></li>
	 * <li><B>verify: the overall checkbox is checked</B></li>
	 * <li><B>verify: individual checkbox is checked</B></li>
	 * <li><B>verify: the overall status is completed</B></li>
	 * <li><B>uncheck the individual checkbox of the creator</B></li>
	 * <li><B>verify: the overall checkbox is unchecked</B></li>
	 * <li><B>verify: individual checkbox is unchecked</B></li>
	 * <li><B>verify: the overall status is uncompleted</B></li>
	 * <li><B>login with the owner</B></li>
	 * <li><B>complete the todo by using the overall checkbox</B></li>
	 * <li><B>verify: the force complete dialog popup</B></li>
	 * <li><B>click ok</B></li>
	 * <li><B>verify: the overall checkbox is checked</B></li>
	 * <li><B>verify: individual checkbox is checked</B></li>
	 * <li><B>verify: the overall status is completed</B></li>
	 * <li><B>check in TO DO LIST->Incomplete To Do</B></li>
	 * <li><B>verify: the todo is not in the view</B></li>
	 * <li><B>check in TO DO LIST->Completed To Do Items</B></li>
	 * <li><B>verify: the todo is in the view</B></li>
	 * </ul>
	 */
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})			
	public void multipleToDoForceComplete(){

		String testName = ui.startTest();
	
		User internalUser1 = cfg.getUserAllocator().getUser();
		User internalUser2 = cfg.getUserAllocator().getUser();
		User internalUser3 = cfg.getUserAllocator().getUser();
		
		ActivityMember author1 = new ActivityMember(ActivityRole.AUTHOR, internalUser2, ActivityMember.MemberType.PERSON);		
		ActivityMember author2 = new ActivityMember(ActivityRole.AUTHOR, internalUser3, ActivityMember.MemberType.PERSON);
		
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, internalUser1.getAttribute(cfg.getLoginPreference()), internalUser1.getPassword());		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS,author1.getUser()))
													.addMember(new Member(CommunityRole.MEMBERS,author2.getUser()))													
													.shareOutside(false)
													.access(Access.RESTRICTED)
													.build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);				
		
		BaseActivity activity = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.build();
        
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(internalUser1);

		log.info("INFO: Create an activity");
		activity.createInCommunity(ui);
		
		ui.reloginCommunityActivity(author1.getUser(), activity);

		BaseActivityToDo todo = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.multipleAssignTo(internalUser1)
		.multipleAssignTo(author1.getUser())
		.multipleAssignTo(author2.getUser())
		.build();
		ui.createToDo(todo);
		String todoUUID = ui.getEntryUUID(todo);
		ui.expandEntry(todoUUID);
		String ownerUserID = getUserIDFromAssignee(todoUUID, internalUser1.getDisplayName());
		String author1UserID = getUserIDFromAssignee(todoUUID, author1.getUser().getDisplayName());
		String author2UserID = getUserIDFromAssignee(todoUUID, author2.getUser().getDisplayName());				

		log.info("INFO: login with the other author, complete the todo by using the overall checkbox");
		ui.reloginCommunityActivity(author2.getUser(), activity);
		ui.checkOverallCheckBox(todoUUID);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh("Are you sure you want to mark this To Do Item as complete?"),"Error: should not see force complete dialog");
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, author2UserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(!ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status should not be completed");

		log.info("INFO: login with the creator, complete the todo by using the overall checkbox");
		ui.reloginCommunityActivity(author1.getUser(), activity);		
		
		ui.checkOverallCheckBox(todoUUID);
		Assert.assertTrue(ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?"),"Error: Doesn't see force complete dialog");
		ui.clickMarkCompleteButton();
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, author2UserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status isn't completed");		

		log.info("INFO: uncheck the individual checkbox of the creator");		
		ui.uncheckIndividualCheckBox(todoUUID, author1UserID);
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todoUUID, author1UserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox should not be checked");		
		Assert.assertTrue(!ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status should not be completed");						

		log.info("INFO: login with the owner,complete the todo by using the overall checkbox");		
		ui.reloginCommunityActivity(internalUser1, activity);		
		
		ui.checkOverallCheckBox(todoUUID);
		Assert.assertTrue(ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?"),"Error: Doesn't see force complete dialog");
		ui.clickMarkCompleteButton();
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todoUUID),"Error: the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todoUUID, ownerUserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todoUUID),"Error: the overall todo status isn't completed");

		ui.gotoToDoListMainPage();
		ui.clickLinkWait(ActivitiesUIConstants.OverallInCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(todo.getTitle()));
		ui.clickLinkWait(ActivitiesUIConstants.OverallCompletedToDos);
		Assert.assertTrue(ui.fluentWaitTextPresent(todo.getTitle()));		
		ui.endTest();		
		
	}	
	
	/**
	 * <ul>	
	 * <li><B>infor: edit and save a todo without change assignee should not change todo status</B></li>
	* <li><B>create a community and add two authors</B></li>
	* <li><B>create an activity with owner, set all community members as explicit author</B></li>
	* <li><B>create one todo with author and add all members as assignees</B></li>
	* <li><B>complete the todo with each assignee</B></li>
	* <li><B>verify: the overall status is completed</B></li>
	* <li><B>user the creator to edit and save the entry</B></li>
	* <li><B>verify: the overall status is still completed</B></li>
	* <li><B>create another todo with another author and add all member as assignees</B></li>
	* <li><B>complete the todo with one common assignee</B></li>
	* <li><B>log back as the creator to force complete the todo</B></li>
	* <li><B>verify: the overall checkbox is checked</B></li>
	* <li><B>verify: individual checkbox is checked</B></li>
	* <li><B>verify: the overall status is completed</B></li>
	* <li><B>edit and save the entry</B></li>
	* <li><B>verify: the overall status is still completed</B></li>
	 * </ul>
	 */ 
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})	
	public void multipleToDoNoChangeSave(){
		String testName = ui.startTest();
		
		User communityOwner = cfg.getUserAllocator().getUser();
		User communityMember1 = cfg.getUserAllocator().getUser();
		User communityMember2 = cfg.getUserAllocator().getUser();
		
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, communityOwner.getAttribute(cfg.getLoginPreference()), communityOwner.getPassword());		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS,communityMember1))
													.addMember(new Member(CommunityRole.MEMBERS,communityMember2))													
													.shareOutside(false)
													.access(Access.RESTRICTED)
													.build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);		

		BaseActivity activity1 = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.addMember(new ActivityMember(ActivityRole.AUTHOR,communityMember1, ActivityMember.MemberType.PERSON))
				.addMember(new ActivityMember(ActivityRole.AUTHOR,communityMember2, ActivityMember.MemberType.PERSON))
				.build();

		ui.loadComponent(Data.getData().ComponentActivities);

		ui.login(communityOwner);
		activity1.createInCommunity(ui);
		
		log.info("INFO: create one todo with author and add all members as assignees");		
		ui.reloginCommunityActivity(communityMember1, activity1);
		BaseActivityToDo todo1 = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.multipleAssignTo(communityOwner)
		.multipleAssignTo(communityMember1)
		.multipleAssignTo(communityMember2)
		.build();
		ui.createToDo(todo1);
		String todo1UUID = ui.getEntryUUID(todo1);
		ui.expandEntry(todo1UUID);
		String ownerUserID = getUserIDFromAssignee(todo1UUID, communityOwner.getDisplayName());
		String member1ID = getUserIDFromAssignee(todo1UUID, communityMember1.getDisplayName());
		String member2ID = getUserIDFromAssignee(todo1UUID, communityMember2.getDisplayName());
		ui.checkIndividualCheckBox(todo1UUID, member1ID);
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo1UUID, member1ID),"Error: the individual checkbox isn't checked");

		ui.reloginCommunityActivity(communityMember2, activity1);		
		ui.checkIndividualCheckBox(todo1UUID, member2ID);
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo1UUID, member2ID),"Error: the individual checkbox isn't checked");
		ui.reloginCommunityActivity(communityOwner, activity1);
		ui.checkIndividualCheckBox(todo1UUID, ownerUserID);
		Assert.assertTrue(ui.getOverallToDoStatus(todo1UUID),"Error: the overall todo status isn't completed");

		log.info("INFO: user the creator to edit and save the entry");		
		ui.reloginCommunityActivity(communityMember1, activity1);		
		ui.editEntryLink(todo1UUID);
		ui.clickSaveButton();
		Assert.assertTrue(ui.getOverallToDoStatus(todo1UUID),"Error: the overall todo status isn't completed");		

		log.info("INFO: create another todo with another author and add all member as assignees");		
		ui.reloginCommunityActivity(communityMember2, activity1);
		
		BaseActivityToDo todo2 = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.multipleAssignTo(communityOwner)
		.multipleAssignTo(communityMember1)
		.multipleAssignTo(communityMember2)
		.build();
		ui.createToDo(todo2);
		String todo2UUID = ui.getEntryUUID(todo2);
		log.info("INFO: complete the todo with one common assignee");
		ui.reloginCommunityActivity(communityMember1, activity1);
		
		ui.checkIndividualCheckBox(todo2UUID, member1ID);
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo2UUID, member1ID),"Error: the individual checkbox isn't checked");

		log.info("INFO: log back as the creator to force complete the todo");		
		ui.reloginCommunityActivity(communityMember2, activity1);
		ui.checkOverallCheckBox(todo2UUID);
		ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?");
		ui.clickMarkCompleteButton();

		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo2UUID, member2ID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");	
		
		log.info("INFO: edit and save the entry");		
		ui.editEntryLink(todo2UUID);
		ui.clickSaveButton();
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox isn't checked");
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>info: edit todo and change assignee then impact the todo status</B></li>
	 * <li><B>normal complete todo:</B></li>
	 * <li><B>create a community and add two members</B></li>
	 * <li><B>create an activity with owner and add all community as the author</B></li>
	 * <li><B>create a todo with one author and assign it the himself and the other author</B></li>
	 * <li><B>complete the todo with each assignee</B></li>
	 * <li><B>verify: the overall status is completed</B></li>
	 * <li><B>with the creator, edit the todo by adding activity owner as the new assignee</B></li>
	 * <li><B>verify: the overall status is still completed</B></li>
	 * <li><B>uncheck the individual todo</B></li>
	 * <li><B>check the individual todo</B></li>
	 * <li><B>verify: the overall status is not completed</B></li>
	 * <li><B>edit the todo again by removing the activity owner from the assignees</B></li>
	 * <li><B>verify the overall status become to completed</B></li>
	 * <li><B>force complete todo:</B></li>
	 * <li><B>create another todo with one author and assign it the himself and the other author</B></li>
	 * <li><B>as the creator, force complete the todo</B></li>
	 * <li><B>edit the todo and add activity owner as the new assignee</B></li>
	 * <li><B>verify the overall status is still complete</B></li>
	 * <li><B>edit the todo again by removing the activity owner from the assignees</B></li>
	 * <li><B>verify the overall status is still complete	</B></li>
	 * </ul>
	 */
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})	
	public void multipleToDoAssigneeChangeSave(){

		User internalUser1 = cfg.getUserAllocator().getUser();
		User internalUser2 = cfg.getUserAllocator().getUser();
		User internalUser3 = cfg.getUserAllocator().getUser();
		
		String intUser1 = internalUser1.getDisplayName();
		String testName = ui.startTest();
		ActivityMember author1 = new ActivityMember(ActivityRole.AUTHOR, internalUser2, ActivityMember.MemberType.PERSON);		
		ActivityMember author2 = new ActivityMember(ActivityRole.AUTHOR, internalUser3, ActivityMember.MemberType.PERSON);
		
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, internalUser1.getAttribute(cfg.getLoginPreference()), internalUser1.getPassword());		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS,author1.getUser()))
													.addMember(new Member(CommunityRole.MEMBERS,author2.getUser()))													
													.shareOutside(false)
													.access(Access.RESTRICTED)
													.build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);				
		
		BaseActivity activity = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.build();
        
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(internalUser1);

		log.info("INFO: Create an activity");
		activity.createInCommunity(ui);
		
		ui.reloginCommunityActivity(author1.getUser(), activity);		
		BaseActivityToDo todo1 = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.multipleAssignTo(author1.getUser())
		.multipleAssignTo(author2.getUser())
		.build();
		ui.createToDo(todo1);
		String todo1UUID = ui.getEntryUUID(todo1);
		ui.expandEntry(todo1UUID);
		String author1UserID = getUserIDFromAssignee(todo1UUID, author1.getUser().getDisplayName());
		String author2UserID = getUserIDFromAssignee(todo1UUID, author2.getUser().getDisplayName());

		log.info("INFO: complete the todo with each assignee");		
		ui.checkIndividualCheckBox(todo1UUID, author1UserID);
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo1UUID, author1UserID),"Error: the individual checkbox isn't checked");
		ui.reloginCommunityActivity(author2.getUser(), activity);		
		ui.checkIndividualCheckBox(todo1UUID, author2UserID);
		Assert.assertTrue(ui.getOverallToDoStatus(todo1UUID),"Error: the overall todo status isn't completed");

		log.info("INFO: with the creator, edit the todo by adding activity owner as the new assignee");			
		ui.reloginCommunityActivity(author1.getUser(), activity);
		
		ui.addNewAssignee(todo1UUID, intUser1);
/* 		new design add new assigne will not change the complelted todo to incomplete */
		Assert.assertTrue(ui.getOverallToDoStatus(todo1UUID),"Error: the overall todo status isn't completed");

		log.info("INFO: uncheck and check the individual todo");		
		ui.uncheckIndividualCheckBox(todo1UUID, author1UserID);
		ui.checkIndividualCheckBox(todo1UUID, author1UserID);

		log.info("INFO: edit the todo again by removing the activity owner from the assignees");		
		String ownerUserID= getUserIDFromAssignee(todo1UUID, intUser1);
		ui.removeAssignee(todo1UUID, ownerUserID);
		Assert.assertTrue(ui.getOverallToDoStatus(todo1UUID),"Error: the overall todo status isn't completed");		

		log.info("INFO: create another todo with one author and assign it the himself and the other author");		
		BaseActivityToDo todo2 = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.multipleAssignTo(author1.getUser())
		.multipleAssignTo(author2.getUser())
		.build();
		ui.createToDo(todo2);
		String todo2UUID = ui.getEntryUUID(todo2);

		log.info("INFO: as the creator, force complete the todo");
		ui.checkOverallCheckBox(todo2UUID);
		ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?");
		ui.clickMarkCompleteButton();

		log.info("INFO: edit the todo and add activity owner as the new assignee, new design: add new assigne will not change the complelted todo to incomplete");		
		ui.addNewAssignee(todo2UUID, intUser1);
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");
		ui.removeAssignee(todo2UUID, ownerUserID);
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");		
		ui.endTest();
	}
	
	/**
	* <ul>	 
	* <li><B>infor: complete/uncheck todo for a fully completed todo.</B></li>
	* <li><B>normal completed todo:</B></li>
	* <li><B>create a community and add two members</B></li>
	* <li><B>create an activity with owner and add all community as the author</B></li>
	* <li><B>create a todo with one author and assign it to himself and the other author</B></li>
	* <li><B>complete the todo with each assignee</B></li>
	* <li><B>verify: the overall status is completed</B></li>
	* <li><B>use the normal assignee to uncheck the individual todo</B></li>
	* <li><B>verify: the overall status is incomplete</B></li>
	* <li><B>recheck the individual todo</B></li>
	* <li><B>verify: the overall status is complete</B></li>
	* <li><B>use the creator to uncheck the individual todo</B></li>
	* <li><B>verify: the overall status is incomplete</B></li>
	* <li><B>recheck the individual todo</B></li>
	* <li><B>verify: the overall status is complete</B></li>
	* <li><B>force completed todo:</B></li>
	* <li><B>create another todo and assign it to all members</B></li>
	* <li><B>force complete the todo</B></li>
	* <li><B>login with the normal assignee</B></li>
	* <li><B>verify: the overall checkbox is not checked</B></li>
	* <li><B>verify: individual checkbox is not checked</B></li>
	* <li><B>verify: the overall status is completed</B></li>
	* <li><B>check the overall checkbox</B></li>
	* <li><B>verify: the overall checkbox is checked</B></li>
	* <li><B>verify: individual checkbox is checked</B></li>
	* <li><B>verify: the overall status is still completed</B></li>
	* <li><B>uncheck the overall checkbox</B></li>
	* <li><B>verify: the overall checkbox is not checked</B></li>
	* <li><B>verify: individual checkbox is not checked</B></li>
	* <li><B>verify: the overall status is incomplete</B></li>
	* <li><B>login with the creator to force complete the todo again</B></li>
	* <li><B>login with the normal assignee</B></li>
	* <li><B>check the individual checkbox</B></li>
	* <li><B>verify: the overall checkbox is checked</B></li>
	* <li><B>verify: individual checkbox is checked</B></li>
	* <li><B>verify: the overall status is still completed</B></li>
	* <li><B>uncheck the individual checkbox</B></li>
	* <li><B>verify: the overall checkbox is not checked</B></li>
	* <li><B>verify: individual checkbox is not checked</B></li>
	* <li><B>verify: the overall status is incomplete</B></li>
	* <li><B>login with the creator to force complete the todo again</B></li>
	* <li><B>login with the owner as another assignee</B></li>
	* <li><B>verify: the overall checkbox is checked</B></li>
	* <li><B>verify: individual checkbox is not checked</B></li>
	* <li><B>verify: the overall status is completed</B></li>
	* <li><B>check the individual checkbox</B></li>
	* <li><B>verify: the overall checkbox is checked(has defect)</B></li>
	* <li><B>verify: individual checkbox is checked</B></li>
	* <li><B>verify: the overall status is completed</B></li>
	* <li><B>(add refresh as the workaround for the defect)</B></li>
	* <li><B>uncheck the overall checkbox</B></li>
	* <li><B>verify: the overall checkbox is not checked</B></li>
	* <li><B>verify: individual checkbox is not checked</B></li>
	* <li><B>verify: the overall status is incomplete</B></li>
	* <li><B>login with the creator to force complete the todo again</B></li>
	* <li><B>login with the owner as another assignee</B></li>
	* <li><B>check the individual checkbox</B></li>
	* <li><B>(add refresh as the workaround for the defect)</B></li>
	* <li><B>uncheck the individual checkbox</B></li>
	* <li><B>verify: the overall checkbox is not checked</B></li>
	* <li><B>verify: individual checkbox is not checked</B></li>
	* <li><B>verify: the overall status is incomplete</B></li>
	* </ul>
	*/
	@Test(groups = { "regression", "fvtonprem", "fvtcloud"})		
	public void multipleToDoPostCompletedChange(){
		String testName = ui.startTest();
		
		User internalUser1 = cfg.getUserAllocator().getUser();
		User internalUser2 = cfg.getUserAllocator().getUser();
		User internalUser3 = cfg.getUserAllocator().getUser();
		
		ActivityMember author1 = new ActivityMember(ActivityRole.AUTHOR, internalUser2, ActivityMember.MemberType.PERSON);		
		ActivityMember author2 = new ActivityMember(ActivityRole.AUTHOR, internalUser3, ActivityMember.MemberType.PERSON);
		
		APICommunitiesHandler comApiOwner;
		comApiOwner = new APICommunitiesHandler(serverURL, internalUser1.getAttribute(cfg.getLoginPreference()), internalUser1.getPassword());		
		BaseCommunity community = new BaseCommunity.Builder("comm" + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS,author1.getUser()))
													.addMember(new Member(CommunityRole.MEMBERS,author2.getUser()))													
													.shareOutside(false)
													.access(Access.RESTRICTED)
													.build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.ACTIVITIES);				
		
		BaseActivity activity = new BaseActivity.Builder(testName
				+ Helper.genDateBasedRand())
				.community(community)
				.build();
        
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(internalUser1);

		log.info("INFO: Create an activity");
		activity.createInCommunity(ui);
		
		ui.reloginCommunityActivity(author1.getUser(), activity);
		
		BaseActivityToDo todo1 = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.multipleAssignTo(author1.getUser())
		.multipleAssignTo(author2.getUser())
		.build();
		ui.createToDo(todo1);
		String todo1UUID = ui.getEntryUUID(todo1);
		ui.expandEntry(todo1UUID);
		String author1UserID = getUserIDFromAssignee(todo1UUID, author1.getUser().getDisplayName());
		String author2UserID = getUserIDFromAssignee(todo1UUID, author2.getUser().getDisplayName());

		log.info("INFO: complete the todo with each assignee");
		ui.checkIndividualCheckBox(todo1UUID, author1UserID);
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo1UUID, author1UserID),"Error: the individual checkbox isn't checked");

		ui.reloginCommunityActivity(author2.getUser(), activity);
		ui.checkIndividualCheckBox(todo1UUID, author2UserID);
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo1UUID),"Error: the overall checkbox isn't checked");

		log.info("INFO: use the normal assignee to uncheck the individual todo");		
		ui.uncheckIndividualCheckBox(todo1UUID, author2UserID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo1UUID),"Error:the overall checkbox should not be checked");
		ui.checkIndividualCheckBox(todo1UUID, author2UserID);
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo1UUID),"Error: the overall checkbox isn't checked");
		log.info("INFO: try more with overall checkbox for normal assignee");		
		ui.uncheckOverallCheckBox(todo1UUID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo1UUID),"Error:the overall checkbox should not be checked");		
		ui.checkOverallCheckBox(todo1UUID);
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo1UUID),"Error: the overall checkbox isn't checked");		
		
		log.info("INFO: use the creator to uncheck the individual todo");				
		ui.reloginCommunityActivity(author1.getUser(), activity);
		ui.uncheckIndividualCheckBox(todo1UUID, author1UserID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo1UUID),"Error:the overall checkbox should not be checked");
		ui.checkIndividualCheckBox(todo1UUID, author1UserID);
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo1UUID),"Error: the overall checkbox isn't checked");		
		ui.uncheckOverallCheckBox(todo1UUID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo1UUID),"Error:the overall checkbox should not be checked");				

		log.info("INFO: force completed todo");						
		BaseActivityToDo todo2 = BaseActivityToDo.builder(testName + " todo" + Helper.genDateBasedRandVal())
		.tags(Helper.genDateBasedRandVal())
		.multipleAssignTo(author1.getUser())
		.multipleAssignTo(author2.getUser())
		.multipleAssignTo(internalUser1)
		.build();
		ui.createToDo(todo2);
		String todo2UUID = ui.getEntryUUID(todo2);
		ui.expandEntry(todo2UUID);
		String ownerUserID = getUserIDFromAssignee(todo1UUID, internalUser1.getDisplayName());
		ui.checkOverallCheckBox(todo2UUID);
		ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?");
		ui.clickMarkCompleteButton();
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error:the overall checkbox isn't checked");
		
		log.info("INFO: as normal assignee, check/uncheck the overall checkbox");		
		ui.reloginCommunityActivity(author2.getUser(), activity);
		ui.expandEntry(todo2UUID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox should not be checked");
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");			
		ui.checkOverallCheckBox(todo2UUID);
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");
		ui.uncheckOverallCheckBox(todo2UUID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox should not be checked");
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(!ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status should not be completed");
		
		log.info("INFO: login with the creator to force complete the todo again");		
		ui.reloginCommunityActivity(author1.getUser(), activity);
		ui.checkOverallCheckBox(todo2UUID);
		ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?");
		ui.clickMarkCompleteButton();		
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error:the overall checkbox isn't checked");
		
		log.info("INFO: as normal assignee, check/uncheck the individual checkbox");			
		ui.reloginCommunityActivity(author2.getUser(), activity);
		ui.checkIndividualCheckBox(todo2UUID,author2UserID);
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox isn't checked");
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");
		ui.uncheckIndividualCheckBox(todo2UUID,author2UserID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox should not be checked");
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(!ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status should not be completed");
		
		log.info("INFO: login with the creator to force complete the todo again");					
		ui.reloginCommunityActivity(author1.getUser(), activity);
		ui.checkOverallCheckBox(todo2UUID);
		ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?");
		ui.clickMarkCompleteButton();
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error:the overall checkbox isn't checked");

		log.info("INFO: login with the owner as another assignee, check the individual checkbox,uncheck the overall checkbox");
		ui.reloginCommunityActivity(internalUser1,activity);
		ui.expandEntry(todo2UUID);
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox isn't checked");
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");		
		ui.checkIndividualCheckBox(todo2UUID, ownerUserID);
/*		defect_blocking
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox isn't checked");
	*/	
		Assert.assertTrue(ui.getIndividualCheckBoxStatus(todo2UUID, ownerUserID),"Error: the individual checkbox isn't checked");
		Assert.assertTrue(ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status isn't completed");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Outline);
		ui.uncheckOverallCheckBox(todo2UUID);
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox should not be checked");
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(!ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status should not be completed");
		
		log.info("INFO: login with the creator to force complete the todo again");		
		ui.reloginCommunityActivity(author1.getUser(), activity);
		ui.checkOverallCheckBox(todo2UUID);
		ui.fluentWaitTextPresent("Are you sure you want to mark this To Do Item as complete?");
		ui.clickMarkCompleteButton();
		Assert.assertTrue(ui.getOverallCheckBoxStatus(todo2UUID),"Error:the overall checkbox isn't checked");

		log.info("INFO: login with the owner as another assignee, check/uncheck the individual checkbox");				
		ui.reloginCommunityActivity(internalUser1,activity);
		ui.checkIndividualCheckBox(todo2UUID, ownerUserID);
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Outline);
		ui.uncheckIndividualCheckBox(todo2UUID, ownerUserID);
		
		Assert.assertTrue(!ui.getOverallCheckBoxStatus(todo2UUID),"Error: the overall checkbox should not be checked");
		Assert.assertTrue(!ui.getIndividualCheckBoxStatus(todo2UUID, author2UserID),"Error: the individual checkbox should not be checked");
		Assert.assertTrue(!ui.getOverallToDoStatus(todo2UUID),"Error: the overall todo status should not be completed");		
		
	}
	private String createFile(User apiUser) {
		APIFileHandler apiFileOwner = new APIFileHandler(serverURL,	apiUser.getAttribute(cfg.getLoginPreference()), apiUser.getPassword());

		String file1 = Data.getData().file1;
		String fileName = "File_" + Helper.genDateBasedRand();
		
		BaseFile baseFile = new BaseFile.Builder(file1).extension(".jpg")
				.rename(fileName)
				.build();
		
		String filePath =  "resources/" + baseFile.getName();
		log.info("path of image is " + filePath);
		File file = new File(filePath);

		FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file);
		Assert.assertTrue(fileEntry != null,
				"Failed to upload file using API.");
		return fileName+".jpg";
	}
	
	

	private String createFolder(User apiUser) {
		APIFileHandler apiFileOwner = new APIFileHandler(serverURL, apiUser.getAttribute(cfg.getLoginPreference()), apiUser.getPassword());

		String folderName = "Folder_" + Helper.genDateBasedRand();

		BaseFile baseFolder = new BaseFile.Builder(folderName)
		.tags(Helper.genDateBasedRand())
		.shareLevel(ShareLevel.EVERYONE)
		.build();

		String filePath = cfg
				.getTestConfig()
				.getBrowserEnvironment()
				.getAbsoluteFilePath(cfg.getUploadFilesDir(),
						Data.getData().file1);

		File file = new File(filePath);

		FileEntry folderEntry = new FileEntry(file, baseFolder.getName(),
				baseFolder.getName(), baseFolder.getTags(), Permissions.PUBLIC,
				true, Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.EDIT, "shareSummary", null);

		log.info("INFO: Creating folder");
		folderEntry = baseFolder.createFolderAPI(apiFileOwner, folderEntry);

		Assert.assertTrue(folderEntry != null,
				"Failed to create folder using API.");
		return folderName;
	}

    /**
     * get user's ID from the assignee UI in todo 
     * @param todoUUID
     * @param assigneeName
     * @return user uuid
     */
    private String getUserIDFromAssignee(String todoUUID, String assigneeName){
    	if(cfg.getProductName().toLowerCase().equals("cloud")){
	    	Element e = driver.getFirstElement("css=a[class='fn lotusPerson']:contains('"+assigneeName+"')");
	    	Assert.assertTrue(e!=null,"cannot find assignee "+ assigneeName);
	    	String href = e.getAttribute("href");
	    	return href.substring(href.indexOf("view/")+5);
	    }else{
	    	Element e = driver.getFirstElement("css=a[class='fn lotusPerson bidiAware']:contains('"+assigneeName+"')");
	    	Assert.assertTrue(e!=null,"cannot find assignee "+ assigneeName);
	    	String href = e.getAttribute("href_bc_");
	    	return href.substring(href.indexOf("userid=")+7);
	    }
    }	
}
