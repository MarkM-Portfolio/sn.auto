package com.ibm.conn.auto.tests.communities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;

public class BVT_Level_2_Communities_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities_MT_Boundary.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private APIActivitiesHandler apiActivitiesHandler;
	private APIFileHandler apiFilesHandler;
	private User testUser_orgA, testUser_orgB;
	private String serverURL_MT_orgA, serverURL_MT_orgB;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test that an orgA user is not able to add or invite users from orgB as members in communities</li>
	*<li><B>Step:</B>Create a public community in orgA using API</li>
	*<li><B>Step: </B>Load the Communities component in orgA and login as an orgA user</li>
	*<li><B>Step: </B>Navigate to the community using its UUID</li>
	*<li><B>Step: </B>Navigate to the Members tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Click on the 'Add Members' button to start adding members to the existing community</li>
	*<li><B>Step: </B>Select the option 'Members' in the 'Member Role' dropdown</li>
	*<li><B>Step: </B>Type in an orgB user's first name in the text box</li>
	*<li><B>Verify: </B>The message 'No results found' appears after typing an orgB user's first name in the text field</li>
	*<li><B>Step: </B>Click on the option 'Person not listed? Use full search...' in the type ahead</li>
	*<li><B>Verify: </B>The message 'No results found for: Amy.' appears in the type ahead</li>
	*<li><B>Step: </B>Click on the Cancel button/li>
	*<li><B>Step: </B>Click on the 'Invite Members' button to start inviting members to the existing community</li>
	*<li><B>Step: </B>Type in an orgB user's first name in the text box</li>
	*<li><B>Verify: </B>The message 'No results found' appears after typing an orgB user's first name in the text field</li>
	*<li><B>Step: </B>Click on the option 'Person not listed? Use full search...' in the type ahead</li>
	*<li><B>Verify: </B>The message 'No results found for: Amy.' appears in the type ahead</li>
	*<li><B>Step: </B>Delete the community created earlier</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadAddAndInviteAnOrgbUserToAnOrgaCommunity() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		Member member = new Member(CommunityRole.MEMBERS, testUser_orgB);

		//Build the community to be created later in orgA
		log.info("INFO: Building Community in orgA");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC)
				.description("Test description for testcase " + testName).build();

		log.info("INFO: Initiate the community API Handler for: " + testUser_orgA.getDisplayName());
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());

		log.info("INFO: Create a public community in orgA using API");
		logger.strongStep("Create a public community in orgA using API");
		Community community = baseCom.createAPI(apiHandler);

		log.info("INFO: Get UUID of the community: " + baseCom.getName());
		logger.strongStep("Get UUID of the community: " + baseCom.getName());
		baseCom.setCommunityUUID(baseCom.getCommunityUUID_API(apiHandler, community));

		log.info("INFO: Load the Communities component in orgA and login as: " + testUser_orgA.getDisplayName());
		logger.strongStep("Load the Communities component in orgA and login as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("Navigate to the community using UUID");
		baseCom.navViaUUID(ui);

		log.info("INFO: Click on the Members tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Members tab using the navigation menu of the community");
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);
		//ui.clickLinkWait(CommunitiesUI.leftNavMembers);

		log.info("INFO: Click on the 'Add Members' button to start adding members to the existing community");
		logger.strongStep("Click on the 'Add Members' button to start adding members to the existing community");
		ui.clickLinkWait(CommunitiesUIConstants.AddMembersToExistingCommunity);

		log.info("INFO: Select the option '" + member.getRole().toString() + "' in the 'Member Role' dropdown");
		logger.strongStep("Select the option '" + member.getRole().toString() + "' in the 'Member Role' dropdown");
		ui.clickLinkWait(CommunitiesUIConstants.CommunityMembersDropdown);
		driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown()
				.selectOptionByVisibleText(member.getRole().toString());

		log.info("INFO: Enter an orgB user's first name in the text box");
		logger.strongStep("Enter an orgB user's first name in the text box");
		ui.typeTextWithDelay(CommunitiesUIConstants.AddMembersToExistingTypeAhead, testUser_orgB.getFirstName());

		log.info("INFO: Verify the message 'No results found' appears after typing an orgB user's first name");
		logger.strongStep("Verify the message 'No results found' appears after typing an orgB user's first name");
		Assert.assertTrue(driver.isTextPresent("No results found"),
				"The message 'No results found' does not appear after typing an orgB user's first name");

		log.info("INFO: Click on the option 'Person not listed? Use full search...' in the type ahead");
		logger.strongStep("Click on the option 'Person not listed? Use full search...' in the type ahead");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.MemberSelectUserSearch);

		log.info("INFO: Verify that 'No results found for: " + testUser_orgB.getFirstName() + ".' message should be displayed");
		logger.strongStep("Validate that 'No results found for: " + testUser_orgB.getFirstName() + ".' message should be displayed");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.NoResultFoundFor));
		Element ele = driver.getFirstElement(CommunitiesUIConstants.NoResultFoundFor);
		logger.strongStep("Msg is:" + ele.getText());
		log.info("Msg is:"+ele.getText());
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.NoResultFoundFor).getText(),
				"No results found for: " + testUser_orgB.getFirstName() + ".",
				"The message 'No results found for: " + testUser_orgB.getFirstName() + ".' does not appear on the page");

		log.info("INFO: Click on the Cancel button");
		logger.strongStep("Click on the Cancel button");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.MemberCancelButton);

		log.info("INFO: Click on the 'Invite Members' button to start inviting members to the existing community");
		logger.strongStep("Click on the 'Invite Members' button to start inviting members to the existing community");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.InviteMemberButton);

		log.info("INFO: Enter an orgB user's first name in the text box");
		logger.strongStep("Enter an orgB user's first name in the text box");
		ui.typeTextWithDelay(CommunitiesUIConstants.InviteMembersToExistingTypeAhead, testUser_orgB.getFirstName());

		log.info("INFO: Verify the message 'No results found' appears after typing an orgB user's first name");
		logger.strongStep("Verify the message 'No results found' appears after typing an orgB user's first name");
		Assert.assertTrue(driver.isTextPresent("No results found"),
				"The message 'No results found' does not appear after typing an orgB user's first name");

		log.info("INFO: Click on the option 'Person not listed? Use full search...' in the type ahead");
		logger.strongStep("Click on the option 'Person not listed? Use full search...' in the type ahead");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.MemberSelectUserSearch);

		log.info("INFO: Verify that 'No results found for: " + testUser_orgB.getFirstName() + ".' message should be displayed");
		logger.strongStep("Validate that 'No results found for: " + testUser_orgB.getFirstName() + ".' message should be displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.InviteUserNoResultFoundFor));
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.InviteUserNoResultFoundFor).getText(),
				"No results found for: " + testUser_orgB.getFirstName() + ".",
				"The message 'No results found for: " + testUser_orgB.getFirstName() + ".' does not appear on the page");

		log.info("INFO: Delete the community created earlier");
		logger.strongStep("Delete the community created earlier");
		apiHandler.deleteCommunity(community);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Test that an orgA user is not able to add users from orgB as a member during the creation of a community</li>
	*<li><B>Step: </B>Load the Communities component in orgA and login as an orgA user</li>
	*<li><B>Step: </B>Create a new community using the 'Start A Community' dropdown</li>
	*<li><B>Step: </B>Enter the community name</li>
	*<li><B>Step: </B>Select the access for the community as Restricted but with external users allowed</li>
	*<li><B>Step: </B>Click on the 'Access Advanced Features' link if the Members field is not visible</li>
	*<li><B>Step: </B>Enter an orgB user's first name in the search text field</li>
	*<li><B>Verify: </B>The message 'No results found' appears after typing an orgB user's first name</li>
	*<li><B>Step: </B>Click on the option 'Person not listed? Use full search...'</li>
	*<li><B>Verify: </B>The message 'No results found for: Amy.' appears</li>
	*<li><B>Step: </B>Click on the Cancel button</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadAddOrgbMemberInOrgaCommunityCreation() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();

		log.info("INFO: Load the Communities component in orgA and login as: " + testUser_orgA.getDisplayName());
		logger.strongStep("Load the Communities component in orgA and login as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		log.info("INFO: Create a new community using the 'Start A Community' dropdown");
		logger.strongStep("Create a new community using the 'Start A Community' dropdown");
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDown).click();
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDown).click();

		log.info("INFO: Wait for the community page to load");
		logger.strongStep("Wait for the community page to load");
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

		log.info("INFO: Enter the community name as: " + communityName);
		logger.strongStep("Enter the community name as: " + communityName);
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(communityName);

		log.info("INFO: Select the access for the community as Restricted but with external users allowed");
		logger.strongStep("Select the access for the community as Restricted but with external users allowed");
		if(!driver.getSingleElement(CommunitiesUIConstants.CommunityallowExternalBox).isSelected()) {
			driver.getSingleElement(CommunitiesUIConstants.CommunityAccessPrivate).click();
			driver.getSingleElement(CommunitiesUIConstants.CommunityallowExternalBox).click();
		}

		log.info("INFO: Check to see if there is a need to open advanced options");
		logger.strongStep("Check to see if there is a need to open advanced options");
		if(!driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).isVisible()){
			log.info("INFO: Click on the 'Access Advanced Features' link");
			logger.strongStep("Click on the 'Access Advanced Features' link");
			ui.openAdvancedOptions();
		}

		log.info("INFO: Enter an orgB user's first name in the search text field");
		logger.strongStep("Enter an orgB user's first name in the search text field");
		ui.typeTextWithDelay(CommunitiesUIConstants.AddMembersToExistingTypeAhead, testUser_orgB.getFirstName());

		log.info("INFO: Verify the message 'No results found' appears after typing an orgB user's first name");
		logger.strongStep("Verify the message 'No results found' appears after typing an orgB user's first name");
		Assert.assertTrue(driver.isTextPresent("No results found"),
				"The message 'No results found' does not appear after typing an orgB user's first name");

		log.info("INFO: Click on the option 'Person not listed? Use full search...'");
		logger.strongStep("Click on the option 'Person not listed? Use full search...'");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.MemberSelectUserSearch);

		log.info("INFO: Verify that 'No results found' message is displayed");
		logger.strongStep("Validate that 'No results found' message is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.NoResultFoundFor));
		Element ele = driver.getFirstElement(CommunitiesUIConstants.NoResultFoundFor);

		log.info("INFO: Verify that the message that appears is: " + ele.getText());
		logger.strongStep("Verify that the message that appears is: " + ele.getText());
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.NoResultFoundFor).getText(), "No results found for: " + testUser_orgB.getFirstName() + ".",
				"The message 'No results found for: " + testUser_orgB.getFirstName() + ".' does not appear on the page");

		log.info("INFO: Click on the Cancel button");
		logger.strongStep("Cancel the creation of community");
		ui.clickLinkWait(CommunitiesUIConstants.CancelButton);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Test that an orgA user is not able to search for orgB communities while copying a community</li>
	*<li><B>Step:</B>Create communities of different kinds in orgB using API</li>
	*<li><B>Step: </B>Load the Communities component in orgB and login as an orgB user</li>
	*<li><B>Step: </B>Click on the 'Create a Community' dropdown then click on 'Copy an Existing Community'</li>
	*<li><B>Step: </B>Search for the orgB communities to copy</li>
	*<li><B>Verify: </B>The community names appear in the type ahead</li>
	*<li><B>Step: </B>Logout as the current user</li>
	*<li><B>Step: </B>Load the Communities component in orgA and login as an orgA user</li>
	*<li><B>Step: </B>Click on the 'Create a Community' dropdown then click on 'Copy an Existing Community'</li>
	*<li><B>Step: </B>Search for the orgB communities in the text box</li>
	*<li><B>Verify: </B>The message 'No results found' appears in the type ahead</li>
	*<li><B>Step: </B>Delete all the communities that were created in orgB</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadCopyOrgbCommunityToOrga() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		List <Community> orgbCommunities = new ArrayList<Community>();

		//Build the communities to be created later in orgB
		BaseCommunity orgbPublic = new BaseCommunity.Builder("orgbPublic" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbModerated = new BaseCommunity.Builder("orgbModerated" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbExternalRestricted = new BaseCommunity.Builder("orgbExternalRestricted" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbInternalRestricted = new BaseCommunity.Builder("orgbInternalRestricted" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(false).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbRBL = new BaseCommunity.Builder("orgbRBL" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).rbl(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		log.info("INFO: Initiate the communities API Handler for: " + testUser_orgB.getDisplayName());
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(serverURL_MT_orgB, testUser_orgB.getAttribute(cfg.getLoginPreference()),
				testUser_orgB.getPassword());

		log.info("INFO: Create communities of different kinds in orgB using API");
		logger.strongStep("Create communities of different kinds in orgB using API");
		Community communityOrgbPublic = orgbPublic.createAPI(apiHandler);
		Community communityOrgbModerated = orgbModerated.createAPI(apiHandler);
		Community communityOrgbExternalRestricted = orgbExternalRestricted.createAPI(apiHandler);
		Community communityOrgbInternalRestricted = orgbInternalRestricted.createAPI(apiHandler);
		Community communityOrgbRBL = orgbRBL.createAPI(apiHandler);

		log.info("INFO: Add the orgB communities to the list previously created");
		logger.strongStep("Add the orgB communities to the list previously created");
		orgbCommunities.add(communityOrgbPublic);
		orgbCommunities.add(communityOrgbModerated);
		orgbCommunities.add(communityOrgbExternalRestricted);
		orgbCommunities.add(communityOrgbInternalRestricted);
		orgbCommunities.add(communityOrgbRBL);

		log.info("INFO: Load the Communities component in orgB and login as the orgB user: " + testUser_orgB.getDisplayName());
		logger.strongStep("Load the Communities component in orgB and login as the orgB user: " + testUser_orgB.getDisplayName());
		ui.loadComponent(serverURL_MT_orgB, Data.getData().ComponentCommunities);
		ui.login(testUser_orgB);

		log.info("INFO: Click on the 'Create a Community' dropdown then click on 'Copy an Existing Community'");
		logger.strongStep("Click on the 'Create a Community' dropdown then click on 'Copy an Existing Community'");
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDown).click();
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromCopyExistingCardView).click();

		for (Community comm : orgbCommunities) {
			log.info("INFO: Search for the community: " + comm.getTitle() + " and verify that the community name appears in the type ahead");
			logger.strongStep("Search for the community: " + comm.getTitle() + " and verify that the community name appears in the type ahead");
			driver.getSingleElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).clear();
			driver.getSingleElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).typeWithDelay(comm.getTitle());
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.copyCommChooseBySearchingTypeAheadPopup + " > span:contains(" + comm.getTitle() + ")"),
					"The community: " + comm.getTitle() + " created in orgB is not visible in the same organization");
		}

		log.info("INFO: Logout as the current user");
		logger.strongStep("Logout as the current user");
		ui.logout();

		log.info("INFO: Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		logger.strongStep("Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities, true);
		ui.login(testUser_orgA);

		log.info("INFO: Click on the 'Create a Community' dropdown then click on 'Copy an Existing Community'");
		logger.strongStep("Click on the 'Create a Community' dropdown then click on 'Copy an Existing Community'");
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDown).click();
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromCopyExistingCardView).click();

		for (Community comm : orgbCommunities) {
			log.info("INFO: Search for the orgB community: " + comm.getTitle() + " and verify that 'No results found' message appears in the type ahead");
			logger.strongStep("Search for the orgB community: " + comm.getTitle() + " and verify that 'No results found' message appears in the type ahead");
			driver.getSingleElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).clear();
			driver.getSingleElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).typeWithDelay(comm.getTitle());
			Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.moveCommunityTypeaheadPicker).getText(), "No results found",
					"The message 'No results found' does not appear when an orgB community is searched for in orgA");
		}

		log.info("INFO: Delete all communities that were created in orgB");
		logger.strongStep("Delete all communities that were created in orgB");
		for (Community comm : orgbCommunities)
			apiHandler.deleteCommunity(comm);

		ui.endTest();

	}


	/**
	*<ul>
	*<li><B>Info: </B>Test that an orgA user is not able to search for orgB communities in the Discover tab of My Communities page</li>
	*<li><B>Step:</B>Create a public community in orgB using API</li>
	*<li><B>Step: </B>Load the Communities component in orgB and login as an orgB user</li>
	*<li><B>Step: </B>Navigate to the Discover tab</li>
	*<li><B>Step: </B>Expand the View dropdown then select the 'Recently Updated' option</li>
	*<li><B>Step: </B>Search for the orgB community created earlier</li>
	*<li><B>Verify: </B>The community card appears on the page</li>
	*<li><B>Step: </B>Logout as the current user</li>
	*<li><B>Step: </B>Load the Communities component in orgA and login as an orgA user</li>
	*<li><B>Step: </B>Navigate to the Discover tab</li>
	*<li><B>Step: </B>Expand the View dropdown then select the 'Recently Updated' option</li>
	*<li><B>Step: </B>Search for the orgB community created earlier</li>
	*<li><B>Verify: </B>The message 'No communities to display.' appears on the page</li>
	*<li><B>Step: </B>Delete the community that was created in orgB</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadDiscoverOrgbCommunityInOrga() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String searchText = "orgb";

		//Build the public community to be created later in orgB
		BaseCommunity orgbPublic = new BaseCommunity.Builder("orgbPublic" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		log.info("INFO: Initiate the community API Handler for: " + testUser_orgB.getDisplayName());
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(serverURL_MT_orgB, testUser_orgB.getAttribute(cfg.getLoginPreference()),
				testUser_orgB.getPassword());

		log.info("INFO: Create a public community in orgB using API");
		logger.strongStep("Create a public community in orgB using API");
		Community community = orgbPublic.createAPI(apiHandler);

		log.info("INFO: Load the Communities component in orgB and login as the orgB user: " + testUser_orgB.getDisplayName());
		logger.strongStep("Load the Communities component in orgB and login as the orgB user: " + testUser_orgB.getDisplayName());
		ui.loadComponent(serverURL_MT_orgB, Data.getData().ComponentCommunities);
		ui.login(testUser_orgB);

		log.info("INFO: Click on the Discover tab");
		logger.strongStep("Navigate to the Discover tab");
		driver.getSingleElement(CommunitiesUIConstants.topNavDiscoverCardView).click();

		log.info("INFO: Select the 'Recently Updated' option in the View dropdown");
		logger.strongStep("Select the 'Recently Updated' option in the View dropdown");
		ui.clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView);
		ui.clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewRecentlyUpdated);

		log.info("INFO: Search for the community: " + community.getTitle() + " and verify that the community card appears on the page");
		logger.strongStep("Search for the community: " + community.getTitle() + " and verify that the community card appears on the page");
		driver.getSingleElement(CommunitiesUIConstants.catalogFilterCardView).typeWithDelay(searchText);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(CommunitiesUI.getCommunityCardByNameLink(community.getTitle())),
				"The community: " + community.getTitle() + " created in orgB is not visible in the Discover tab of the Communities page of the same organization");

		log.info("INFO: Logout as the current user");
		logger.strongStep("Logout as the current user");
		ui.logout();

		log.info("INFO: Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		logger.strongStep("Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities, true);
		ui.login(testUser_orgA);

		log.info("INFO: Click on the Discover tab");
		logger.strongStep("Navigate to the Discover tab");
		driver.getSingleElement(CommunitiesUIConstants.topNavDiscoverCardView).click();

		log.info("INFO: Select the 'Recently Updated' option in the View dropdown");
		logger.strongStep("Select the 'Recently Updated' option in the View dropdown");
		ui.clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView);
		ui.clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewRecentlyUpdated);

		log.info("INFO: Search for the orgB community: " + community.getTitle() + " and verify that the community card does not appear on the page");
		logger.strongStep("Search for the orgB community: " + community.getTitle() + " and verify that the community card does not appear on the page");
		driver.getSingleElement(CommunitiesUIConstants.catalogFilterCardView).typeWithDelay(searchText);
		Assert.assertTrue(driver.isTextPresent("No communities to display."),
				"The message 'No communities to display.' does not appear in the Discover tab of the Communities page of orgA");

		log.info("INFO: Delete the community that was created in orgB");
		logger.strongStep("Delete the community that was created in orgB");
		apiHandler.deleteCommunity(community);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Test that no results are found when an orgA user tries to mention an orgB user in the community widgets - Recent Updates, Status Updates, Blog, Ideation Blog</li>
	*<li><B>Step:</B>Create a public community in orgA using API</li>
	*<li><B>Step: </B>Add the Blog widget to the community if the CNX version is 6.5</li>
	*<li><B>Step: </B>Add the Ideation Blog widget to the community</li>
	*<li><B>Step: </B>Load the Communities component in orgA and login as an orgA user</li>
	*<li><B>Step: </B>Navigate to the community created earlier</li>
	*<li><B>Step: </B>Navigate to the Recent Updates tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Navigate to the Status Updates tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Navigate to the Blog tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Click on the 'New Entry' button and enter a title for the Entry</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the Description text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the Post button to create the Entry</li>
	*<li><B>Step: </B>Click on the 'Add a Comment' link for the Entry</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the comment text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Navigate to the Ideation Blog tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Navigate to the default Ideation Blog for the community</li>
	*<li><B>Step: </B>Click on the 'New Idea' button and enter a title for the Idea</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the Description text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the Post button to create the Idea</li>
	*<li><B>Step: </B>Click on the 'Add a Comment' link for the Idea</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the comment text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Delete the community that was created in orgA</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadMentionOrgbUserInOrgaCommunityWidgetsFirst() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String commonMentionText = "This is a mention for @" + testUser_orgB.getFirstName();

		//Build the public community to be created later in orgA
		BaseCommunity orgaPublicFirst = new BaseCommunity.Builder("orgaPublicFirst" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC)
				.description("Test description for testcase " + testName).build();

		log.info("INFO: Initiate the community API Handler for: " + testUser_orgA.getDisplayName());
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());

		log.info("INFO: Create a public community in orgA using API");
		logger.strongStep("Create a public community in orgA using API");
		Community communityOrgaPublicFirst = orgaPublicFirst.createAPI(apiHandler);

		log.info("INFO: Get UUID of the community: " + orgaPublicFirst.getName());
		logger.strongStep("Get UUID of the community: " + orgaPublicFirst.getName());
		orgaPublicFirst.setCommunityUUID(orgaPublicFirst.getCommunityUUID_API(apiHandler, communityOrgaPublicFirst));

		if(apiHandler.setup.getServiceConfig().isCnxVersion65()) {
			log.info("INFO: Add the Blog widget to the community: " + orgaPublicFirst.getName() + " if the CNX version is 6.5");
			logger.strongStep("Add the Blog widget to the community: " + orgaPublicFirst.getName() + " if the CNX version is 6.5");
			orgaPublicFirst.addWidgetAPI(communityOrgaPublicFirst, apiHandler, BaseWidget.BLOG);
		}

		log.info("INFO: Add the Ideation Blog widget to the community: " + orgaPublicFirst.getName());
		logger.strongStep("Add the Ideation Blog widget to the community: " + orgaPublicFirst.getName());
		orgaPublicFirst.addWidgetAPI(communityOrgaPublicFirst, apiHandler, BaseWidget.IDEATION_BLOG);

		log.info("INFO: Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		logger.strongStep("Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities, true);
		ui.login(testUser_orgA);

		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("Navigate to the community using UUID");
		orgaPublicFirst.navViaUUID(ui);

		log.info("INFO: Click on the Recent Updates tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Recent Updates tab using the navigation menu of the community");
		Community_TabbedNav_Menu.RECENT_UPDATES.select(ui);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		//Navigate to the Status Updates tab using the navigation menu of the community
		log.info("INFO: Click on the Status Updates tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Status Updates tab using the navigation menu of the community");
		Community_TabbedNav_Menu.STATUSUPDATES.select(ui);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		//Navigate to the Blog tab using the navigation menu of the community
		log.info("INFO: Click on the Blog tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Blog tab using the navigation menu of the community");
		Community_TabbedNav_Menu.BLOG.select(ui);

		//Click on the 'New Entry' button
		logger.strongStep("Click on the 'New Entry' button");
		log.info("INFO: Click on the 'New Entry' button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

		//Enter the title for the Entry
		logger.strongStep("Enter the title for the Entry");
		log.info("INFO: Enter the title for the Entry");
		ui.typeText(BlogsUIConstants.BlogsNewEntryTitle, "New Entry");

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		//Click on the Post button to create the Entry
		log.info("INFO: Click on the Post button to create the Entry");
		logger.strongStep("Click on the Post button to create the Entry");
		ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
		ui.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);

		//Click on the 'Add a Comment' link for the Entry
		log.info("INFO: Click on the 'Add a Comment' link for the Entry");
		logger.strongStep("Click on the 'Add a Comment' link for the Entry");
		ui.clickLinkWithJavascript(BlogsUIConstants.BlogsAddACommentLink);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Ideation Blog tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Ideation Blog tab using the navigation menu of the community");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);

		log.info("INFO: Navigate to the default Ideation Blog for the community");
		logger.strongStep("Navigate to the default Ideation Blog for the community");
		ui.clickLinkWithJavascript(BlogsUI.getFile(orgaPublicFirst.getName()));

		log.info("INFO: Click on the 'New Idea' button");
		logger.strongStep("Click on the 'New Idea' button");
		ui.clickLinkWait(BlogsUIConstants.NewIdea);

		log.info("INFO: Enter the title for the Idea");
		logger.strongStep("Enter the title for the Idea");
		ui.typeText(BlogsUIConstants.BlogsNewEntryTitle, "New Entry");

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Post button to create the Idea");
		logger.strongStep("Click on the Post button to create the Idea");
		ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
		ui.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);

		log.info("INFO: Click on the 'Add a Comment' link for the Idea");
		logger.strongStep("Click on the 'Add a Comment' link for the Idea");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Delete the community that was created in orgA");
		logger.strongStep("Delete the community that was created in orgA");
		apiHandler.deleteCommunity(communityOrgaPublicFirst);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Test that no results are found when an orgA user tries to mention an orgB user in the community widgets - Activities, Wiki, Forums, Files and Events</li>
	*<li><B>Step:</B>Create a public community in orgA using API</li>
	*<li><B>Step: </B>Create an Activity in the community using API</li>
	*<li><B>Step: </B>Create a Community File using API</li>
	*<li><B>Step: </B>Add the Activities widget to the community</li>
	*<li><B>Step: </B>Add the Events widget to the community</li>
	*<li><B>Step: </B>Load the Communities component in orgA and login as an orgA user</li>
	*<li><B>Step: </B>Navigate to the community created earlier</li>
	*<li><B>Step: </B>Navigate to the Activities tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Click on the link for the Activity created earlier</li>
	*<li><B>Step: </B>Click on the 'Add Entry' button to start creating a new Entry in the Activity</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the Description text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the Save button to create the Entry</li>
	*<li><B>Step: </B>Click on the 'Add To Do Item' button to start creating a new To Do Item in the Activity</li>
	*<li><B>Step: </B>Click on the 'More Options' link and type the mention for an orgB user in the Description text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the Save button to create the To Do Item</li>
	*<li><B>Step: </B>Navigate to the Wiki tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Click on the 'New Page' link in the left column</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the Description text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the 'Save and Close' button to create a new page in the wiki</li>
	*<li><B>Step: </B>Click on the 'Add a comment' link</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the comment text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Navigate to the Forums tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Click on the 'Start a Topic' button and enter a title for the topic</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the Description text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the Save button to create a new topic in the Forum</li>
	*<li><B>Step: </B>Click on the 'Reply to Topic' button</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Navigate to the Files tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Click on the Details display button</li>
	*<li><B>Step: </B>Click on the link for the file created earlier</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the comment text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Navigate to the Events tab using the navigation menu of the community</li>
	*<li><B>Step: </B>Click on the 'Create an Event' button on the Events page</li>
	*<li><B>Step: </B>Enter the title in the 'Event Title' text box</li>
	*<li><B>Step: </B>Type the mention for an orgB user in the Description text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the Save button to create the Event</li>
	*<li><B>Step: </B>Click on the link for the Event just created</li>
	*<li><B>Step: </B>Click on the 'Add a comment...' link and type the mention for an orgB user in the comment text area</li>
	*<li><B>Verify: </B>No results are displayed in the type ahead</li>
	*<li><B>Step: </B>Click on the Save button to save the comment</li>
	*<li><B>Step: </B>Delete the community that was created in orgA</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadMentionOrgbUserInOrgaCommunityWidgetsSecond() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String commonMentionText = "This is a mention for @" + testUser_orgB.getFirstName();
		String forumTopicTitle = "New Forum Topic";
		String eventTitle = "New Community Event";

		//Build the public community to be created later in orgA
		BaseCommunity orgaPublicSecond = new BaseCommunity.Builder("orgaPublicSecond" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC)
				.description("Test description for testcase: " + testName).build();

		//Build the community activity to be created later in orgA
		BaseActivity communityActivity = new BaseActivity.Builder("orgaPublicActivity" + Helper.genDateBasedRand())
				.goal("Goal for: " + testName)
				.community(orgaPublicSecond)
				.build();

		//Build the community file to be created later in orgA
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				.rename("New File")
				.comFile(true)
				.extension(".jpg")
				.build();

		log.info("INFO: Initiate the community API Handler for: " + testUser_orgA.getDisplayName());
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());

		log.info("INFO: Initiate the community activities API Handler for: " + testUser_orgA.getDisplayName());
		apiActivitiesHandler = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());

		log.info("INFO: Initiate the community files API Handler for: " + testUser_orgA.getDisplayName());
		apiFilesHandler = new APIFileHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());

		log.info("INFO: Create a public community in orgA using API");
		logger.strongStep("Create a public community in orgA using API");
		Community communityOrgaPublicSecond = orgaPublicSecond.createAPI(apiHandler);

		log.info("INFO: Get UUID of the community: " + orgaPublicSecond.getName());
		logger.strongStep("Get UUID of the community: " + orgaPublicSecond.getName());
		orgaPublicSecond.setCommunityUUID(orgaPublicSecond.getCommunityUUID_API(apiHandler, communityOrgaPublicSecond));

		log.info("INFO: Create an Activity in the community using API");	
		logger.strongStep("Create an Activity in the community using API");
		communityActivity.createAPI(apiActivitiesHandler, orgaPublicSecond);

		log.info("INFO: Create a Community File using API");
		logger.strongStep("Create a Community File using API");
		File file = new File(FilesUI.getFileUploadPath(fileA.getName(), cfg));
		fileA.createAPI(apiFilesHandler, file, communityOrgaPublicSecond);

		log.info("INFO: Add the Activities widget to the community: " + orgaPublicSecond.getName());
		logger.strongStep("Add the Activities widget to the community: " + orgaPublicSecond.getName());
		orgaPublicSecond.addWidgetAPI(communityOrgaPublicSecond, apiHandler, BaseWidget.ACTIVITIES);

		log.info("INFO: Add the Events widget to the community: " + orgaPublicSecond.getName());
		logger.strongStep("Add the Events widget to the community: " + orgaPublicSecond.getName());
		orgaPublicSecond.addWidgetAPI(communityOrgaPublicSecond, apiHandler, BaseWidget.EVENTS);

		log.info("INFO: Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		logger.strongStep("Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities, true);
		ui.login(testUser_orgA);

		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("Navigate to the community using UUID");
		orgaPublicSecond.navViaUUID(ui);

		log.info("INFO: Click on the Activities tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Activities tab using the navigation menu of the community");
		Community_TabbedNav_Menu.ACTIVITIES.select(ui);

		log.info("INFO: Click on the link for the Activity: " + communityActivity.getName());
		logger.strongStep("Click on the link for the Activity: " + communityActivity.getName());
		ui.clickLinkWithJavascript(ActivitiesUI.getActivityLink(communityActivity));

		log.info("INFO: Click on the 'Add Entry' button to start creating a new Entry in the Activity");
		logger.strongStep("Click on the 'Add Entry' button to start creating a new Entry in the Activity");
		ui.clickLinkWithJavascript(ActivitiesUIConstants.New_Entry);

		//Type the mention for an orgB user in the Description text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Save button to create the Entry");
		logger.strongStep("Click on the Save button to create the Entry");
		ui.clickLinkWithJavascript(BaseUIConstants.SaveButton);

		log.info("INFO: Click on the 'Add To Do Item' button to start creating a new To Do Item in the Activity");
		logger.strongStep("Click on the 'Add To Do Item' button to start creating a new To Do Item in the Activity");
		ui.clickLinkWithJavascript(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Click on the 'More Options' link");
		logger.strongStep("Click on the 'More Options' link");
		ui.clickLinkWithJavascript(ActivitiesUIConstants.ToDo_More_Options);

		//Type the mention for an orgB user in the Description text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Save button to create the To Do Item");
		logger.strongStep("Click on the Save button to create the To Do Item");
		ui.clickLinkWithJavascript(BaseUIConstants.SaveButton);

		log.info("INFO: Click on the Wiki tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Wiki tab using the navigation menu of the community");
		Community_TabbedNav_Menu.WIKI.select(ui);

		log.info("INFO: Click on the 'New Page' link in the left column");
		logger.strongStep("Click on the 'New Page' link in the left column");
		ui.clickLinkWithJavascript("link=New Page");

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the 'Save and Close' button to create a new page in the wiki");
		logger.strongStep("Click on the 'Save and Close' button to create a new page in the wiki");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.rteSave);

		log.info("INFO: Click on the 'Add a comment' link");
		logger.strongStep("Click on the 'Add a comment' link");
		ui.clickLinkWithJavascript(WikisUIConstants.Add_Comment_Link);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Forums tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Forums tab using the navigation menu of the community");
		Community_TabbedNav_Menu.FORUMS.select(ui);

		log.info("INFO: Click on the 'Start a Topic' button");
		logger.strongStep("Click on the 'Start a Topic' button");
		ui.clickLinkWait(ForumsUIConstants.Start_A_Topic);

		log.info("INFO: Enter a title for the Forum Topic");
		logger.strongStep("Enter a title for the Forum Topic");
		driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Title).type(forumTopicTitle);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Save button to create a new topic in the Forum");
		logger.strongStep("Click on the Save button to create a new topic in the Forum");
		ui.clickLinkWithJavascript(ForumsUIConstants.Save_Forum_Topic_Button);

		log.info("INFO: Click on the 'Reply to Topic' button");
		logger.strongStep("Click on the 'Reply to Topic' button");
		ui.clickLinkWithJavascript(ForumsUIConstants.Reply_to_topic);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Files tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Files tab using the navigation menu of the community");
		Community_TabbedNav_Menu.FILES.select(ui);

		log.info("INFO: Click on the Details display button");
		logger.strongStep("Click on the Details display button");
		Files_Display_Menu.DETAILS.select(ui);

		log.info("INFO: Click on the link for the file created earlier");
		logger.strongStep("Click on the link for the file created earlier");
		ui.clickLinkWithJavascript((FilesUI.getFileIsUploaded(fileA)));

		//Type the mention for an orgB user in the comment text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Events tab in the navigation menu of the community");
		logger.strongStep("Navigate to the Events tab using the navigation menu of the community");
		Community_TabbedNav_Menu.EVENTS.select(ui);

		log.info("INFO: Click on the 'Create an Event' button on the Events page");
		logger.strongStep("Click on the 'Create an Event' button on the Events page");
		ui.clickLinkWithJavascript(CalendarUI.CreateEvent);

		log.info("INFO: Enter the title in the 'Event Title' text box");
		logger.strongStep("Enter the title in the 'Event Title' text box");
		driver.getSingleElement(CalendarUI.EventTitle).type(eventTitle);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Save button to create the Event");
		logger.strongStep("Click on the Save button to create the Event");
		ui.clickLinkWithJavascript(CalendarUI.EventSubmit);

		log.info("INFO: Click on the link for the Event just created");
		logger.strongStep("Click on the link for the Event just created");
		ui.clickLinkWithJavascript("link=" + eventTitle);

		log.info("INFO: Click on the 'Add a comment...' link");
		logger.strongStep("Click on the 'Add a comment...' link");
		ui.clickLinkWithJavascript(CalendarUI.AddAComment);

		//Type the mention for an orgB user in the text area and validate that no results are displayed
		typeAndValidateMentionForOrgbUserInCkEditor(logger, commonMentionText);

		log.info("INFO: Click on the Save button to save the comment");
		logger.strongStep("Click on the Save button to save the comment");
		ui.clickLinkWithJavascript(CalendarUI.AddCommentSaveButton);

		log.info("INFO: Delete the community that was created in orgA");
		logger.strongStep("Delete the community that was created in orgA");
		apiHandler.deleteCommunity(communityOrgaPublicSecond);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Test that an orgA user is not able to move an orgA community as a subcommunity to an orgB community</li>
	*<li><B>Step:</B>Create a public and moderated community of in orgA using API</li>
	*<li><B>Step:</B>Create communities of different kinds in orgB using API</li>
	*<li><B>Step: </B>Load the Communities component in orgA and login as an orgA user</li>
	*<li><B>Step: </B>Navigate to the Moderated community in orgA using UUID</li>
	*<li><B>Step: </B>Click on the 'Community Actions' menu</li>
	*<li><B>Step: </B>Click on the 'Move Community' link in the 'Community Actions' menu</li>
	*<li><B>Step: </B>Enter the text 'org' in the 'Make this a subcommunity of' text box</li>
	*<li><B>Verify: </B>The type ahead contains the name of the orgA public community but not of the orgB communities</li>
	*<li><B>Step: </B>Delete all communities that were created in orgA and orgB</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadMoveAnOrgaCommunityToAnOrgbCommunity() throws Exception {

		APICommunitiesHandler apiHandlerOrga, apiHandlerOrgb;

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		List <Community> orgbCommunities = new ArrayList<Community>();

		//Build the communities to be created later in orgA
		BaseCommunity orgaPublic = new BaseCommunity.Builder("orgaPublic" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgaModerated = new BaseCommunity.Builder("orgaModerated" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		//Build the communities to be created later in orgB
		BaseCommunity orgbPublic = new BaseCommunity.Builder("orgbPublic" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbModerated = new BaseCommunity.Builder("orgbModerated" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbExternalRestricted = new BaseCommunity.Builder("orgbExternalRestricted" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbInternalRestricted = new BaseCommunity.Builder("orgbInternalRestricted" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(false).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbRBL = new BaseCommunity.Builder("orgbRBL" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).rbl(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		//Instantiate API Handler for orgA
		log.info("INFO: Initiate the communities API Handler for: " + testUser_orgA.getDisplayName());
		apiHandlerOrga = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());

		//Instantiate API Handler for orgB
		log.info("INFO: Initiate the communities API Handler for: " + testUser_orgB.getDisplayName());
		apiHandlerOrgb = new APICommunitiesHandler(serverURL_MT_orgB, testUser_orgB.getAttribute(cfg.getLoginPreference()),
				testUser_orgB.getPassword());

		log.info("INFO: Create a public and a Moderated community in orgA using API");
		logger.strongStep("Create a public and a Moderated community in orgA using API");
		Community communityOrgaPublic = orgaPublic.createAPI(apiHandlerOrga);
		Community communityOrgaModerated = orgaModerated.createAPI(apiHandlerOrga);

		log.info("INFO: Create communities of different kinds in orgB using API");
		logger.strongStep("Create communities of different kinds in orgB using API");
		Community communityOrgbPublic = orgbPublic.createAPI(apiHandlerOrgb);
		Community communityOrgbModerated = orgbModerated.createAPI(apiHandlerOrgb);
		Community communityOrgbExternalRestricted = orgbExternalRestricted.createAPI(apiHandlerOrgb);
		Community communityOrgbInternalRestricted = orgbInternalRestricted.createAPI(apiHandlerOrgb);
		Community communityOrgbRBL = orgbRBL.createAPI(apiHandlerOrgb);

		log.info("INFO: Add the orgB communities to the list previously created");
		logger.strongStep("Add the orgB communities to the list previously created");
		orgbCommunities.add(communityOrgbPublic);
		orgbCommunities.add(communityOrgbModerated);
		orgbCommunities.add(communityOrgbExternalRestricted);
		orgbCommunities.add(communityOrgbInternalRestricted);
		orgbCommunities.add(communityOrgbRBL);

		log.info("INFO: Get UUID of the Moderated orgA community: " + orgaModerated.getName());
		logger.strongStep("Get UUID of the Moderated orgA community: " + orgaModerated.getName());
		orgaModerated.setCommunityUUID(orgaModerated.getCommunityUUID_API(apiHandlerOrga, communityOrgaModerated));

		log.info("INFO: Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		logger.strongStep("Load the Communities component in orgA and login as the orgA user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		log.info("INFO: Navigate to the Moderated community in orgA using UUID");
		logger.strongStep("Navigate to the Moderated community in orgA using UUID");
		orgaModerated.navViaUUID(ui);

		log.info("INFO: Click on the 'Community Actions' menu");
		logger.strongStep("Click on the 'Community Actions' menu");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.communityActions);

		log.info("INFO: Click on the 'Move Community' link in the 'Community Actions' menu");
		logger.strongStep("Click on the 'Move Community' link in the 'Community Actions' menu");
		ui.clickLinkWithJavascript(BaseUIConstants.Menu_Item_MoveComunity);

		log.info("INFO: Enter the text 'org' in the 'Make this a subcommunity of' text box");
		logger.strongStep("Enter the text 'org' in the 'Make this a subcommunity of' text box");
		ui.typeTextWithDelay(CommunitiesUIConstants.copyCommChooseBySearchingTextField, "org");

		log.info("INFO: Verify that the type ahead contains the name of the orgA public community but not of the orgB communities");
		logger.strongStep("Verify that the type ahead contains the name of the orgA public community but not of the orgB communities");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTypeAheadPopup).getText().contains(communityOrgaPublic.getTitle()),
				"The orgA public community is not there in the type ahead results");
		for (Community comm : orgbCommunities)
			Assert.assertTrue(!driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTypeAheadPopup).getText().contains(comm.getTitle()),
					"The orgB community: " + comm.getTitle() + " is available in the type ahead results");

		log.info("INFO: Delete all communities that were created in orgA and orgB");
		logger.strongStep("Delete all communities that were created in orgA and orgB");
		apiHandlerOrga.deleteCommunity(communityOrgaPublic);
		apiHandlerOrga.deleteCommunity(communityOrgaModerated);
		for (Community comm : orgbCommunities)
			apiHandlerOrgb.deleteCommunity(comm);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Type a mention for an orgB user in the text area for CK Editor iframe and validate that no results are displayed</li>
	*<li><B>Step:</B>Wait for the CK Editor iframe to appear</li>
	*<li><B>Step:</B>Switch to the CK Editor iframe</li>
	*<li><B>Step: </B>Type the mention text in the text area</li>
	*<li><B>Verify: </B>The message 'No results found' appears after typing an orgB user's first name in the text field</li>
	*<li><B>Step: </B>Click on the option 'Person not listed? Use full search...'</li>
	*<li><B>Verify: </B>The message 'No results found' appears after performing the full search</li>
	*</ul>
	*/
	public void typeAndValidateMentionForOrgbUserInCkEditor(DefectLogger logger, String commonMentionText) {

		log.info("INFO: Wait for " + cfg.getFluentwaittime() + " seconds for the CK Editor iframe to appear");
		logger.strongStep("Wait for " + cfg.getFluentwaittime() + " seconds for the CK Editor iframe to appear");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);

		log.info("INFO: Switch to the CK Editor iframe");
		logger.strongStep("Switch to the iframe: " + BaseUIConstants.StatusUpdate_iFrame + " to start using the text area");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);

		log.info("INFO: Type the mention text '" + commonMentionText + "' in the text area");
		logger.strongStep("Enter the mention text '" + commonMentionText + "' in the text area");
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(commonMentionText);
		driver.switchToFrame().returnToTopFrame();

		log.info("INFO: Verify the message 'No results found' appears after typing an orgB user's first name");
		logger.strongStep("Verify the message 'No results found' appears after typing an orgB user's first name");
		Assert.assertTrue(driver.getSingleElement(HomepageUIConstants.NoResultFound).getText().equals("No results found"),
				"The message 'No results found' does not appear after typing an orgB user's first name");

		log.info("INFO: Click on the option 'Person not listed? Use full search...'");
		logger.strongStep("Click on the option 'Person not listed? Use full search...'");
		ui.clickLinkWithJavascript(HomepageUIConstants.nameSearchList);

		log.info("INFO: Verify the message 'No results found' appears after performing the full search");
		logger.strongStep("Verify the message 'No results found' appears after performing the full search");
		Assert.assertTrue(driver.getSingleElement(HomepageUIConstants.NoResultFound).getText().equals("No results found"),
				"The message 'No results found' does not appear after performing the full search");

	}

}
