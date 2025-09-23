package com.ibm.conn.auto.tests.communities.regression;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.Executor.Alert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityMember extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Member.class);
	private CommunitiesUI ui;
	private HomepageUI hUI;
	private TestConfigCustom cfg;	
	private User testUser,testUser1,testUser2;
	private APICommunitiesHandler apiOwner;
	private FilesUI uiFile;
	private User testUser3;
	private User testUser4;
	private User testUser5;
	private User testUser6;
	private User testUser7;
	private User testUser8;
	private User testUser9;
	private User testUser10;
	private String serverURL;

	/**
	 * PTC_VerifyAbilityToAddMembers 
	 * PTC_VerifyAbilityToInvitemembers
	 * PTC_VerifyMemberExportAndImport
	 * PTC_AbilityToImportUserViaEmailAddress
	 * PTC_FilterSortByFindMemberInCommunity
	 * PTC_VerifyEditMemberDialog
	 * PTC_DisplayUserProfile
	 */
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users

		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		testUser5 = cfg.getUserAllocator().getUser();
		testUser6 = cfg.getUserAllocator().getUser();
		testUser7 = cfg.getUserAllocator().getUser();
		testUser8 = cfg.getUserAllocator().getUser();
		testUser9 = cfg.getUserAllocator().getUser();
		testUser10 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
	}
	
	
	@BeforeMethod(alwaysRun=true )
	public void setUp() throws Exception {

		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		uiFile = FilesUI.getGui(cfg.getProductName(), driver);

	}

	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part1: Verify ability to add members (1 of 5)</li>
	 *<li><B>Info:</B> Test to validate that the creator of the community is listed under members section</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & from UI Try to add one Member to the community</li>
	 *<li><B>Step:</B> Login as creator</li>
	 *<li><B>Step:</B> Open community</li>
	 *<li><B>Step:</B> Select Member from left navigation menu</li> 
	 *<li><B>Verify:</B> Validate creator is present in the list of members</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3AFF7770BEC8C885257C8D006BBFA5">TTT-MEMBERS PAGE PART1: VERIFY ABILITY TO ADD MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void creatorListedUnderMembers() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test Start Page for community " + testName)
												   .build();
				
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Validate that the creator is listed as a member");
		Assert.assertTrue(ui.isElementPresent("link=" + testUser.getDisplayName()),
						  "ERROR: Creator of the community is not listed under members");
				
		ui.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part1: Verify ability to add members (2 of 5)</li>
	 *<li><B>Info:</B> Test to validate that a user with Member access can be added to the community </li>
	 *<li><B>Step:</B> Create a Public community as owner using API & from UI Try to add one Member to the community</li>
	 *<li><B>Step:</B> Login as owner</li> 
	 *<li><B>Step:</B> Open community using communityUUID</li>
	 *<li><B>Step:</B> Select Role Member</li>
	 *<li><B>Step:</B> Select add member and type users display name</li>
	 *<li><B>Step:</B> Select search icon in drop down menu</li>
	 *<li><B>Verify:</B> Owner should be able to see the new Member name in type ahead</li>
	 *<li><B>Step:</B> Select user from type ahead drop down menu</li>
	 *<li><B>Verify:</B> The Member is added with a blue box</li>
	 *<li><B>Step:</B> Select Save button</li>
	 *<li><B>Verify:</B> Validate that the new member role is member</li>
	 *<li><B>Verify:</B> Member has been added to community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3AFF7770BEC8C885257C8D006BBFA5">TTT-MEMBERS PAGE PART1: VERIFY ABILITY TO ADD MEMBERS</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void addMemberCommunity() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test Start Page for community " + testName)
												   .build();
				
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Add Member to community");		
		ui.addMemberCommunity(member);
		
		// Verify the user appears in a blue box below the Add fields
		log.info("INFO: Validate that the user appears in blue box below the add fields to be added to a community");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.selectedUserToAddToComm),
						  "ERROR: Members blue box is not present");
		
		//select save button
		log.info("INFO: Select save button");
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);

		log.info("INFO: Collect the members text from member page");
		String memberInfo = ui.getMemberElement(member).getText();
		
		log.info("INFO: Validating the user is an member");
		Assert.assertTrue(memberInfo.contains("Member"),
						  "ERROR: User does record does not contain owner");
		
		log.info("INFO: Validate that the member is not added to the community");		
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: Member has not been added to the community");
		
		ui.endTest();
	}

	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part1: Verify ability to add members (3 of 5)</li>
	 *<li><B>Info:</B> Test to validate that a user with Owner access can be added to the community</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & from UI Try to add one Member to the community</li>
	 *<li><B>Step:</B> Login as owner</li> 
	 *<li><B>Step:</B> Open community using communityUUID</li>
	 *<li><B>Step:</B> Select Role Owner</li>
	 *<li><B>Step:</B> Select add member and type users display name</li>
	 *<li><B>Step:</B> Select search icon in drop down menu</li>
	 *<li><B>Verify:</B> Owner should be able to see the new Member name in type ahead</li>
	 *<li><B>Step:</B> Select user from type ahead drop down menu</li>
	 *<li><B>Verify:</B> The Member is added with a blue box</li>
	 *<li><B>Step:</B> Select Save button</li>
	 *<li><B>Verify:</B> Validate that the new member role is Owner</li>
	 *<li><B>Verify:</B> Member has been added to community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3AFF7770BEC8C885257C8D006BBFA5">TTT-MEMBERS PAGE PART1: VERIFY ABILITY TO ADD MEMBERS</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void addOwnerCommunity() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.OWNERS, testUser1);
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test Start Page for community " + testName)
												   .build();
				
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Add Member to community");		
		ui.addMemberCommunity(member);
		
		// Verify the user appears in a blue box below the Add fields
		log.info("INFO: Validate that the user appears in blue box below the add fields to be added to a community");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.selectedUserToAddToComm),
						  "ERROR: Members blue box is not present");
		
		//select save button
		log.info("INFO: Select save button");
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);

		log.info("INFO: Collect the members text from member page");
		String memberInfo = ui.getMemberElement(member).getText();
		
		log.info("INFO: Validating the user is an member");
		Assert.assertTrue(memberInfo.contains("Owner"),
						  "ERROR: User does record does not contain owner");
		
		log.info("INFO: Validate that the member is not added to the community");		
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: Member has not been added to the community");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part1: Verify ability to add members (4 of 5)</li>
	 *<li><B>Info:</B> Cancel adding a user by selecting the x before adding</li>
	 *<li><B>Step:</B> Create a Public community as owner using API</li>
	 *<li><B>Step:</B> Login as owner</li> 
	 *<li><B>Step:</B> Open community</li>
	 *<li><B>Step:</B> Select one member</li> 
	 *<li><B>Step:</B> Remove the user by selecting user via blue box</li>
	 *<li><B>Verify:</B> Validate that the user blue box with user name is no longer visible</li>
	 *<li><B>Step:</B> Select save button</li>
	 *<li><B>Verify:</B> Verify the canceled user is not in Member list</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3AFF7770BEC8C885257C8D006BBFA5">TTT-MEMBERS PAGE PART1: VERIFY ABILITY TO ADD MEMBERS</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void cancelBlueBoxAddMember() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test Start Page for community " + testName)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);

		// Member addition through UI				
		log.info("INFO: Add member to community");
		ui.addMemberCommunity(member);
				
		log.info("INFO: Removing the member from the Add member screen by selecting the x");
		ui.clickLinkWait(CommunitiesUI.cancelAddMemberUsingX(member));
		
		//Verify the user is able to remove the Member
		log.info("INFO: Validate user was removed");
		Assert.assertFalse(ui.isElementPresent("link=" + member.getUser().getDisplayName()+" (Members)"),
						   "ERROR: User was not canceled");
		
		log.info("INFO: Select the save button");
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);
	
		log.info("INFO: Validate that the user is  not listed as a member");
		Assert.assertFalse(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: Canceled user was listed under members");
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part1: Verify ability to add members (5 of 5)</li>
	 *<li><B>Info:</B> Cancel adding a user by selecting the Cancel button before adding</li>
	 *<li><B>Step:</B> Create a Public community as owner using API</li>
	 *<li><B>Step:</B> Login as owner</li> 
	 *<li><B>Step:</B> Open community</li>
	 *<li><B>Step:</B> Select one member</li> 
	 *<li><B>Step:</B> Click the Cancel button</li>
	 *<li><B>Verify:</B> Verify the canceled user is not in Member list</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3AFF7770BEC8C885257C8D006BBFA5">TTT-MEMBERS PAGE PART1: VERIFY ABILITY TO ADD MEMBERS</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void cancelButtonAddMember() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test Start Page for community " + testName)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);

		// Member addition through UI				
		log.info("INFO: Add member to community");
		ui.addMemberCommunity(member);

		log.info("INFO: Click the Cancel button");
		ui.clickLink(CommunitiesUIConstants.MemberCancelButton);
	
		log.info("INFO: Validate that the user is  not listed as a member");
		Assert.assertFalse(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: Canceled user was listed under members");
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Test Scenario: Members Page Part2: Verify ability to invite members (1 of 7)</li>
	 *<li><B>Info:</B> Invite member to a community and ensure the invited user shows under proper locations</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open community</li>
	 *<li><B>Step:</B> Invite a Member to the community using Invited Tab</li>
	 *<li><B>Verify:</B> Then check if invited member is present</li>
	 *<li><B>Verify:</B> The invited member is present under the invited text area Blue Box</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C7747979E19AE7C85257C8D006D0D38">TTT-MEMBERS PAGE PART2: VERIFY ABILITY TO INVITE MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void inviteMemberBlueBox() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test Start Page for community " + testName)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		// Addition of member via Invited Tab
		log.info("INFO: Adding member via Invited member button");
		ui.inviteMemberCommunity(member);
								
		// To test if the invited member is present under the Invited member addition text box
		// if the listed user does not match the format of firstname lastname, it will check
		// to see if it matches the smartcloud format (displayName, email address)
		log.info("INFO: Validate that the selected member blue text box is present");
		
		boolean listedUser=(ui.isElementPresent("link=" + member.getUser().getDisplayName()));
		if (!listedUser)
		{
			//
			String listedUser1=ui.getElementText(CommunitiesUIConstants.selectedUserToAddToComm);
			log.info("INFO: The listed user is: " + listedUser1 );
			
	        log.info("INFO: SmartCloud:Verifying the invited user text box displays");
			String userName=(member.getUser().getDisplayName() + " <" + member.getUser().getEmail() + ">");
			
			log.info("INFO: User shown is: " + userName);
			listedUser=listedUser1.equals(userName);
			}
			
		else log.info("INFO: On-Prem:verifying invited user text box displays");

		Assert.assertTrue(listedUser,"ERROR: The invited user is not listed");
		
		//delete community
				log.info("INFO: Removing community");
				community.delete(ui, testUser);
	
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Test Scenario: Members Page Part2: Verify ability to invite members (2 of 7)</li>
	 *<li><B>Info:</B> Validate success message and that an invited member is not added</li>
	 *<li><B>Step:</B> Create a Public community as owner using API</li>
	 *<li><B>Step:</B> Invite Member to the community</li>
	 *<li><B>Step:</B> Select send invitation</li>
	 *<li><B>Verify:</B> Validate you receive invite success message</li>
	 *<li><B>Verify:</B> You don't see the Invited Member on this page</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C7747979E19AE7C85257C8D006D0D38">TTT-MEMBERS PAGE PART2: VERIFY ABILITY TO INVITE MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void sendInvitation() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test Start Page for community " + testName)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		log.info("INFO: Adding member via Invited member button");
		ui.inviteMemberCommunity(member);
				
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
			
		log.info("INFO: Validate the success message");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getInviteSuccessMsg(member.getUser())),
				  		  "ERROR: Success message is not present");
		
		log.info("INFO: Validate user is not added to members of community");
		Assert.assertFalse(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
							"ERROR: The invited member is Present on the MemberPage");

		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Test Scenario: Members Page Part2: Verify ability to invite members (3 of 7)</li>
	 *<li><B>Info:</B> Test to be sure invited members are also located on the invited tab</li>
	 *<li><B>Step:</B> Create a Public community as owner using API</li>
	 *<li><B>Step:</B> Invite member to community</li>
	 *<li><B>Step:</B> Switch to Invited tab</li>
	 *<li><B>Verify:</B> You see the invited member under the Invited tab</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C7747979E19AE7C85257C8D006D0D38">TTT-MEMBERS PAGE PART2: VERIFY ABILITY TO INVITE MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void checkInvitationTab() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		log.info("INFO: Adding member via Invited member button");
		ui.inviteMemberCommunity(member);
				
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
				
		//Click on Invitations Tab
		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);
				
		//Test Invited member is present under the Invitations tab
		log.info("INFO: Validate invited member is present under the Invitations Tab");
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: The invited member is not Present on Invitation Tabs");
	
		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Test Scenario: Members Page Part2: Verify ability to invite members (4 of 7)</li>
	 *<li><B>Info:</B> Test the ability to re-send an invitation to a user who was invited to join a community using re-send link</li>
	 *<li><B>Step:</B> Create a Public community using API </li>
	 *<li><B>Step:</B> Invite a Member to the community</li>
	 *<li><B>Step:</B> Go to Invitations tab</li>
	 *<li><B>Step:</B> Select re-send link for user</li>
	 *<li><B>Verify:</B> The re-send link</li>
	 *<li><B>Verify:</B> The message after selecting the re-send link</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C7747979E19AE7C85257C8D006D0D38">TTT-MEMBERS PAGE PART2: VERIFY ABILITY TO INVITE MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void inviteMemberResendLink() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.tabbedNavMembersTab);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		log.info("INFO: Adding member via Invited member button");
		ui.inviteMemberCommunity(member);
				
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
				
		//Click on Invitations Tab
		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);
			
		log.info("INFO: Select the resend link");
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberResendLink);
		
		log.info("INFO: Validate the success message");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getInviteSuccessMsg(member.getUser())),
				  		  "ERROR: Success message is not present");

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part2: Verify ability to invite members (5 of 7)</li>
	 *<li><B>Info:</B> Test the ability to re-send an invitation to a user who was invited to join a community using re-send button</li>
	 *<li><B>Step:</B> Create a Public community using API </li>
	 *<li><B>Step:</B> Invite a Member to the community</li>
	 *<li><B>Step:</B> Go to Invitations tab</li>
	 *<li><B>Step:</B> Select the user check box</li>
	 *<li><B>Step:</B> Select re-send button for user</li>
	 *<li><B>Verify:</B> The re-send button</li>
	 *<li><B>Verify:</B> The message after selecting the re-send button</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C7747979E19AE7C85257C8D006D0D38">TTT-MEMBERS PAGE PART2: VERIFY ABILITY TO INVITE MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void inviteMemberResendButton() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		log.info("INFO: Adding member via Invited member button");
		ui.inviteMemberCommunity(member);
				
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
				
		//Click on Invitations Tab
		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);

		log.info("INFO: Select the user checkbox");
		ui.selectInvitedMember(member);
		
		log.info("INFO: Select the resend link");
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberResendButton);
		
		log.info("INFO: Validate the success message");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getInviteSuccessMsg(member.getUser())),
				  		  "ERROR: Success message is not present");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part2: Verify ability to invite members (6 of 7)</li>
	 *<li><B>Info:</B> Test the ability to revoke an invitation to a user who was invited to join a community using re-send link</li>
	 *<li><B>Step:</B> Create a Public community using API </li>
	 *<li><B>Step:</B> Invite a Member to the community</li>
	 *<li><B>Step:</B> Go to Invitations tab</li>
	 *<li><B>Step:</B> Select revoke link for user</li>
	 *<li><B>Verify:</B> The revoke link</li>
	 *<li><B>Verify:</B> The message after selecting the revoke link</li>
	 *<li><B>Verify:</B> The user is no longer present</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C7747979E19AE7C85257C8D006D0D38">TTT-MEMBERS PAGE PART2: VERIFY ABILITY TO INVITE MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void inviteMemberRevokeLink() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		log.info("INFO: Adding member via Invited member button");
		ui.inviteMemberCommunity(member);
				
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
				
		//Click on Invitations Tab
		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);

		log.info("INFO: Select the resend link");
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberRevokeLink);
		
		log.info("INFO: Select ok");
		ui.clickLinkWait(CommunitiesUIConstants.okButton);
		
		log.info("INFO: Validate that the message This Community has no pending invitations.");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.resendInviteMsg),
						  "ERROR: No pending invitation message is not present");
			
		log.info("INFO: Validate user is no longer present");
		Assert.assertFalse(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
							"ERROR: The invited member is still present on the page");
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part2: Verify ability to invite members (7 of 7)</li>
	 *<li><B>Info:</B> Test the ability to revoke an invitation to a user who was invited to join a community using re-send button</li>
	 *<li><B>Step:</B> Create a Public community using API </li>
	 *<li><B>Step:</B> Invite a Member to the community</li>
	 *<li><B>Step:</B> Go to Invitations tab</li>
	 *<li><B>Step:</B> Select the user check box</li>
	 *<li><B>Step:</B> Select revoke button for user</li>
	 *<li><B>Verify:</B> The revoke button</li>
	 *<li><B>Verify:</B> The message after selecting the revoke button</li>
	 *<li><B>Verify:</B> The user is no longer present</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C7747979E19AE7C85257C8D006D0D38">TTT-MEMBERS PAGE PART2: VERIFY ABILITY TO INVITE MEMBERS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void inviteMemberRevokeButton() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		log.info("INFO: Adding member via Invited member button");
		ui.inviteMemberCommunity(member);
				
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
				
		//Click on Invitations Tab
		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);

		log.info("INFO: Select the user checkbox");
		ui.selectInvitedMember(member);
		
		log.info("INFO: Select the member revoke button");
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberRevokeButton);
		
		log.info("INFO: Select ok");
		ui.clickLinkWait(CommunitiesUIConstants.okButton);
		
		//The invitations have been revoked.
		log.info("INFO: Validate that the message This Community has no pending invitations.");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.resendInviteMsg),
						  "ERROR: No pending invitation message is not present");
			
		log.info("INFO: Validate user is no longer present");
		Assert.assertFalse(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
							"ERROR: The invited member is still present on the page");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Member page Part4: verify ability to import members by csv file (1 of 2)</li> 
	 *<li><B>Test Scenario:</B> Member page Part5: verify ability to export users of a community to a csv file (1 of 2)</li> 
	 *<li><B>Info:</B> Export member of community and import the member back via a CSV File</li> 
	 *<li><B>Step:</B> Create a Public community using API</li>  
	 *<li><B>Step:</B> Login as owner</li>  
	 *<li><B>Step:</B> Open community</li>   
	 *<li><B>Step:</B> Navigate to Members with left navigation menu</li> 
	 *<li><B>Step:</B> Export members to csv file</li> 
	 *<li><B>Step:</B> Delete the users from the community</li> 
	 *<li><B>Step:</B> Import the members we exported to csv file</li> 
	 *<li><B>Verify:</B>Verify the ability to export & import a Member using CSV file</li>
	 *<li><B>Verify:</B>Verify that you get this success message in the green banner
	 *<li><B>Verify:</B>Verify that you see the newly imported users on the "Members" page
	 *<li><B>Verify:</B>Verify that they have been added with "Members" access   
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB42D77A733B3AFE85257C8D006F59B8">TTT-MEMBERS PAGE PART4: VERIFY ABILITY TO IMPORT MEMBERS BY CSV FILE</a></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DB47B5D67CCFDCAE85257C8D006FF6D9">TTT-MEMBERS PAGE PART5: VERIFY ABILITY TO EXPORT MEMBERS (ON_PREM ONLY!)</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void exportAndImportMemberCSV() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member1 = new Member(CommunityRole.MEMBERS, testUser1);
		Member member2 = new Member(CommunityRole.MEMBERS, testUser2);
		Member member3 = new Member(CommunityRole.MEMBERS, testUser3);
		Member member4 = new Member(CommunityRole.MEMBERS, testUser4);
		Member member5 = new Member(CommunityRole.MEMBERS, testUser5);
		Member member6 = new Member(CommunityRole.MEMBERS, testUser6);
		Member member7 = new Member(CommunityRole.MEMBERS, testUser7);
		Member member8 = new Member(CommunityRole.MEMBERS, testUser8);
		Member member9 = new Member(CommunityRole.MEMBERS, testUser9);
		Member member10 = new Member(CommunityRole.MEMBERS, testUser10);
		
		List<Member> Memberslist = Arrays.asList(member1,member2,member3,member4,member5,member6,member7,member8,member9,member10);
		boolean banner;	
			
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		.access(Access.PUBLIC)	
		.tags(Data.getData().commonTag + rndNum )
		.description("Test export & import Members " + testName).addMembers(Memberslist).build();

		// filename of export is fixed by the application as membership.csv
		BaseFile file = new BaseFile.Builder("membership")
				.extension(".csv")
				.build();
			
	
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		if (testConfig.serverIsLegacyGrid())  {
			log.info("INFO: Set the directory for the download and ensure that it does not have old exports.");
			uiFile.setupDirectory();
		}
			
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Export members to a file
		log.info("INFO: Export members to a csv file");
		ui.exportMembersViaFile(file,Memberslist);
					
		//Verify the file has being downloaded - localhost currently
		log.info("INFO: Validate the files has been downloaded");
		uiFile.verifyFileDownloaded(file.getName() + file.getExtension());
		
		//Delete added members
		log.info("INFO: Select remove link");
							
		for (int mem=0;mem<10;mem++)
		{
			ui.getFirstVisibleElement(CommunitiesUIConstants.RemoveMemberCommunity).click();
			ui.clickLinkWait(CommunitiesUIConstants.RevokeInviteOK);
		}
		
		log.info("INFO: Select submit button");
		ui.clickLinkWait(CommunitiesUIConstants.ImportMembersButton);
		
		//Import users from CSV file.				
		log.info("INFO: Select to import the users as Members from the drop down menu");
		driver.getSingleElement(CommunitiesUIConstants.SelectRoleImportMember).useAsDropdown().selectOptionByVisibleText("Members");
		
		log.info("INFO: Import user via CSV file");
		ui.importMembersViaFile(file);
						
		log.info("INFO: Validate message 'You have successfully added the following members to this community ... ' message");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().ImportMemberMsg),
		     "ERROR: You have successfully added the following members to this community does not appear");	 
		banner=(ui.fluentWaitTextPresent(Data.getData().ImportMemberMsg) && ui.fluentWaitTextPresent(testUser1.getDisplayName()) 
		&& ui.fluentWaitTextPresent(testUser2.getDisplayName()) && ui.fluentWaitTextPresent(testUser3.getDisplayName()) 
		&& ui.fluentWaitTextPresent(testUser4.getDisplayName())&& ui.fluentWaitTextPresent(testUser5.getDisplayName())
		&& ui.fluentWaitTextPresent(testUser6.getDisplayName())&& ui.fluentWaitTextPresent(testUser7.getDisplayName())
		&& ui.fluentWaitTextPresent(testUser8.getDisplayName())&& ui.fluentWaitTextPresent(testUser9.getDisplayName())
		&& ui.fluentWaitTextPresent(testUser10.getDisplayName())); 
		Assert.assertTrue(banner,
	    		 "ERROR: You have successfully added the following members to this community with users added does not appear in green banner");
		
		//Verify newly imported users on the "Members" page and has Member access.
		for(int member =0;member<Memberslist.size();member++)
		{
		log.info("INFO: Collect the members text from member page");
		String memberInfo = ui.getMemberElement( Memberslist.get(member)).getText();
			
		log.info("INFO: Validating the user is an Member");
		Assert.assertTrue(memberInfo.contains("Member"),
					 "ERROR: User record does not contain member");
			
		log.info("INFO: Validate that the Member is added to the community");
		Assert.assertTrue(ui.isElementPresent("link=" + ( Memberslist.get(member)).getUser().getDisplayName()),
					  "ERROR: Member has not been added to the community");
			
		}
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}

	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Member page Part4: verify ability to import members by csv file (2 of 2)</li> 
	 *<li><B>Test Scenario:</B> Member page Part5: verify ability to export users of a community to a csv file (2 of 2)</li> 
	 *<li><B>Info:</B> Export an owner of community and import the owner back via a CSV File</li> 
	 *<li><B>Step:</B> Create a Public community using API</li>  
	 *<li><B>Step:</B> Login as owner</li>  
	 *<li><B>Step:</B> Open community</li>   
	 *<li><B>Step:</B> Navigate to Members with left navigation menu</li> 
	 *<li><B>Step:</B> Export members to csv file</li> 
	 *<li><B>Step:</B> Delete a person from the community</li> 
	 *<li><B>Step:</B> Import the members we exported to csv file</li> 
	 *<li><B>Verify:</B> Verify the ability to export/import an Owner using CSV file</li> 
	  *<li><B>Verify:</B>Verify that you get this success message in the green banner
	 *<li><B>Verify:</B>Verify that you see the newly imported users on the "Members" page
	 *<li><B>Verify:</B>Verify that they have been added with "Owners" access 
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB42D77A733B3AFE85257C8D006F59B8">TTT-MEMBERS PAGE PART4: VERIFY ABILITY TO IMPORT MEMBERS BY CSV FILE</a></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DB47B5D67CCFDCAE85257C8D006FF6D9">TTT-MEMBERS PAGE PART5: VERIFY ABILITY TO EXPORT MEMBERS (ON_PREM ONLY!)</a></li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void exportAndImportOwnersCSV() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member owner1 = new Member(CommunityRole.OWNERS, testUser1);
		Member owner2 = new Member(CommunityRole.OWNERS, testUser2);
		Member owner3 = new Member(CommunityRole.OWNERS, testUser3);
		Member owner4 = new Member(CommunityRole.OWNERS, testUser4);
		Member owner5 = new Member(CommunityRole.OWNERS, testUser5);
		Member owner6 = new Member(CommunityRole.OWNERS, testUser6);
		Member owner7 = new Member(CommunityRole.OWNERS, testUser7);
		Member owner8 = new Member(CommunityRole.OWNERS, testUser8);
		Member owner9 = new Member(CommunityRole.OWNERS, testUser9);
		Member owner10 = new Member(CommunityRole.OWNERS, testUser10);
		
		List<Member> Memberslist = Arrays.asList(owner1,owner2,owner3,owner4,owner5,owner6,owner7,owner8,owner9,owner10);
		boolean banner;
		
				
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                                .access(Access.PUBLIC)	
		                                                .tags(Data.getData().commonTag + rndNum )
		                                                .description("Test export & import Owners " + testName).addMembers(Memberslist).build();

		// filename of export is fixed by the application as membership.csv
		BaseFile file = new BaseFile.Builder("membership")
									.extension(".csv")
									.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		if (testConfig.serverIsLegacyGrid())  {
			//Create a directory to export members
			log.info("INFO: Set the directory for the download and ensure that it is empty");
			uiFile.setupDirectory();
		}
			
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);
		
		//Export members to a file
		log.info("INFO: Export members to a csv file");
		ui.exportMembersViaFile(file,Memberslist);
		
		//Verify the file has being downloaded - localhost currently
		log.info("INFO: Validate the files has been downloaded");
		uiFile.verifyFileDownloaded(file.getName() + file.getExtension());
		
		log.info("INFO: Select remove link");
		
		for (int mem=0;mem<10;mem++)
		{
			ui.getFirstVisibleElement(CommunitiesUIConstants.RemoveMemberCommunity).click();
			ui.clickLinkWait(CommunitiesUIConstants.RevokeInviteOK);
		}
				
		log.info("INFO: Select submit button");
		ui.clickLinkWait(CommunitiesUIConstants.ImportMembersButton);
						
		log.info("INFO: Select to import the users as Owners from the drop down menu");
		driver.getSingleElement(CommunitiesUIConstants.SelectRoleImportMember).useAsDropdown().selectOptionByVisibleText("Owners");
		
		log.info("INFO: Import user via CSV file");
		ui.importMembersViaFile(file);
		
		//Verify You have successfully added the following members to this community with users added appears in green banner				
		log.info("INFO: Validate message 'You have successfully added the following members to this community ... ' message");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().ImportMemberMsg),
		     "ERROR: You have successfully added the following members to this community does not appear");	 
		banner=(ui.fluentWaitTextPresent(Data.getData().ImportMemberMsg) && ui.fluentWaitTextPresent(testUser1.getDisplayName()) 
		&& ui.fluentWaitTextPresent(testUser2.getDisplayName()) && ui.fluentWaitTextPresent(testUser3.getDisplayName()) 
		&& ui.fluentWaitTextPresent(testUser4.getDisplayName())&& ui.fluentWaitTextPresent(testUser5.getDisplayName())
		&& ui.fluentWaitTextPresent(testUser6.getDisplayName())&& ui.fluentWaitTextPresent(testUser7.getDisplayName())
		&& ui.fluentWaitTextPresent(testUser8.getDisplayName())&& ui.fluentWaitTextPresent(testUser9.getDisplayName())
		&& ui.fluentWaitTextPresent(testUser10.getDisplayName())); 
		Assert.assertTrue(banner,
	    		 "ERROR: You have successfully added the following members to this community with users added does not appear in green banner");
				
		
		//Verify newly imported users on the "Members" page and has owners access.
		for(int member =0;member<Memberslist.size();member++)
		{
		log.info("INFO: Collect the members text from member page");
		String memberInfo = ui.getMemberElement( Memberslist.get(member)).getText();
			
		log.info("INFO: Validating the user is an Owner");
		Assert.assertTrue(memberInfo.contains("Owner"),
					 "ERROR: User record does not contain Owner");
			
		log.info("INFO: Validate that the Owner is added to the community");
		Assert.assertTrue(ui.isElementPresent("link=" + ( Memberslist.get(member)).getUser().getDisplayName()),
					  "ERROR: Owner has not been added to the community");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
					
		}
	  		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Export Members dialog box</li>
	 * <li><B>Info:</B> This test will verify the UI on the Export Members dialog</li>
	 * <li><B>Step:</B> Create community via the API</li>
	 * <li><B>Step:</B> Log into Communities</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Step:</B> Click on the Export Members button</li>
	 * <li><B>Verify:</B> Verify the Export Members dialog box displays</li>
	 * <li><B>Verify:</B> Verify the following items on the page: header, close icon 'X', Owners & Members checkboxes, Export & Cancel buttons</li>
	 * <li><B>Cleanup:</B> Delete the community using the API </li> 
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/995A8D463CBD19118525800D004854C2/633C7C9F79478C2B852580180062B795"</a></li>
	 *</ul>
	 */			

	@Test(groups = {"regression", "regressioncloud"})
	public void exportMembersDialogUI() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
				
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                                .access(Access.PUBLIC)	
		                                                .tags(Data.getData().commonTag + rndNum )
		                                                .description("Test Export Members dialog UI " + testName).build();
		
		//Create a community using the API
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Get the UUID for the community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
					
		//Open the Community using UUID
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Select Members from the left navigation menu
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Click on the Export Members button
		log.info("INFO: Click on the Export Members button");
		ui.clickLinkWait(CommunitiesUIConstants.ExportMembersButton);
		
		//Verify the Export Members dialog displays by checking for the dialog title
		log.info("INFO: Verify the Export Members dialog displays - check for dialog title");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersDialogHeading),
				"ERROR: Export Members dialog did not display");
		
		//Verify the Close icon 'X' exists on the dialog box
		log.info("INFO: Verify the Close icon 'X' appears on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersDialogCloseIcon),
				"ERROR: The Close icon 'X' does not appear on the dialog box");
		
		//Verify the Owners checkbox exists on the dialog box
		log.info("INFO: Verify the Owners checkbox appears on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersOwnersCheckbox),
				"ERROR: The Owners checkbox does not appear on the dialog box");
		
		//Verify the Owners checkbox is selected by default
		log.info("INFO: Verify the Owners checkbox is selected by default");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersOwnersCheckboxSelected),
				"ERROR: The Owners checkbox is not selected by default");
		
		//Verify the Members checkbox exists on the dialog box
		log.info("INFO: Verify the Members checkbox appears on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersMembersCheckbox),
				"ERROR: The Members checkbox does not appear on the dialog box");
		
		//Verify the Members checkbox is selected by default
			log.info("INFO: Verify the Members checkbox is selected by default");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersMembersCheckboxSelected),
					"ERROR: The Members checkbox is not selected by default");
			
		//Verify the Export button exists on the dialog box
		log.info("INFO: Verify the Export button appears on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersDialogExportButton),
				"ERROR: The Export button does not appear on the dialog box");
		
		//Verify the Cancel button exists on the dialog box
		log.info("INFO: Verify the Cancel button appears on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersDialogCancelButton),
				"ERROR: The Cancel button does not appear on the dialog box");
					
		//Delete the community using the API
		log.info("INFO: Delete community using the API");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part3: Verify ability to import users via email address (1 of 2)</li>
	 *<li><B>Info:</B> Import two members using email address to this community validate that they are added</li>
	 *<li><B>Step:</B> Create a Public community using API</li>  
	 *<li><B>Step:</B> Login as owner</li>  
	 *<li><B>Step:</B> Open community</li>   
	 *<li><B>Step:</B> Navigate to Members with left navigation menu</li> 
	 *<li><B>Step:</B> Select to import members</li> 
	 *<li><B>Step:</B> Type comma separated email address for members</li> 
	 *<li><B>Step:</B> Verify that You have successfully added the following members to this community green banner appears at top end of the screen
	 *<li><B>Verify:</B>Verify that after Import the two users imported are present</li>
	 *<li><B>Verify:</B>Verify users have been added with "Member" access
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D64F3508FB3C81C385257C8D006E6BC8">TTT-MEMBERS PAGE PART3: VERIFY ABILITY TO IMPORT MEMBERS BY EMAIL ADDRESS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void newMembersImport() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member1 = new Member(CommunityRole.MEMBERS, testUser1);
		Member member2 = new Member(CommunityRole.MEMBERS, testUser2);
		boolean banner;
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.build();

		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
			
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
			
		//Click on Import Members button
		log.info("INFO: Click Import members");
		ui.clickLinkWait(CommunitiesUIConstants.ImportMembersButton);
			
		log.info("INFO: Type email address of two members to be imported separated by a comma");
		driver.getSingleElement(CommunitiesUIConstants.MembersImportTextArea).type(testUser1.getEmail()+","+testUser2.getEmail());
			
		log.info("INFO: Select Submit button");
		ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
		
		//Verify You have successfully added the following members to this community with users added appears in green banner
		log.info("INFO: Validate message 'You have successfully added the following members to this community ... ' message");
		banner=(ui.fluentWaitTextPresent(Data.getData().ImportMemberMsg) && ui.fluentWaitTextPresent(testUser2.getDisplayName()) 
			&& ui.fluentWaitTextPresent(testUser1.getDisplayName())); 
			Assert.assertTrue(banner,
		    		 "ERROR: You have successfully added the following members to this community with users added does not appear in green banner");
					
		//Test to verify members are added to the community
		log.info("INFO: Validate that the " + testUser1.getDisplayName() + " was added to the community");
		Assert.assertTrue(ui.isElementPresent("link=" + testUser1.getDisplayName()),
						"ERROR: The imported member is not Present on the MemberPage");
		
		log.info("INFO: Validate that the " + testUser2.getDisplayName() + " was added to the community");
		Assert.assertTrue(ui.isElementPresent("link=" + testUser2.getDisplayName()),
						"ERROR: The imported member is not Present on the MemberPage");
		
		//Test to verify users have been added with "Member" access
		log.info("INFO: Collect the member text from member page");
		String member1Info = ui.getMemberElement(member1).getText();
		
		log.info("INFO: Validating the added user is a member");
		Assert.assertTrue(member1Info.contains("Member"),
						 "ERROR: User does not contain member access");
				
		log.info("INFO: Collect the member text from member page");
		String member2Info = ui.getMemberElement(member2).getText();
		
		log.info("INFO: Validating the added user is a member");
		Assert.assertTrue(member2Info.contains("Member"),
						 "ERROR: User does not contain member access");
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
		}

	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part3: Verify ability to import users via email address (2 of 2)</li>
	 *<li><B>Info:</B> Import two members using email address to this community validate that previous members are still present</li>
	 *<li><B>Step:</B> Create a Public community using API</li>  
	 *<li><B>Step:</B> Login as owner</li>  
	 *<li><B>Step:</B> Open community</li>   
	 *<li><B>Step:</B> Navigate to Members with left navigation menu</li> 
	 *<li><B>Step:</B> Select to import members</li> 
	 *<li><B>Step:</B> Type comma separated email address for members</li> 
	 *<li><B>Verify:</B> After Import of the two users the Owner is still present on the Members page</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D64F3508FB3C81C385257C8D006E6BC8">TTT-MEMBERS PAGE PART3: VERIFY ABILITY TO IMPORT MEMBERS BY EMAIL ADDRESS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void previousMembersImport() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		boolean banner;

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.build();
	
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);
			
		//Click on Import Members button
		log.info("INFO: Click Import members");
		ui.clickLinkWait(CommunitiesUIConstants.ImportMembersButton);
			
		log.info("INFO: Type email address of two members to be imported separated by a comma");
		driver.getSingleElement(CommunitiesUIConstants.MembersImportTextArea).type(testUser1.getEmail() + "," + testUser2.getEmail());
			
		log.info("INFO: Select the submit button");
		ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
					
		//Test "You have successfully added the following members to this community" with users added appears in green banner
		log.info("INFO: You have successfully added the following members to this community");	
		banner=(ui.fluentWaitTextPresent(Data.getData().ImportMemberMsg) && ui.fluentWaitTextPresent(testUser2.getDisplayName())
		       && ui.fluentWaitTextPresent(testUser1.getDisplayName())); 
		Assert.assertTrue(banner,
	    		 "ERROR: You have successfully added the following members to this community with users added does not appear in green banner");
			
		//Test Owner is present on the Members page
		log.info("INFO: Validate that the " + testUser.getDisplayName() + " was added to the community");
		Assert.assertTrue(ui.isElementPresent("link="+testUser.getDisplayName()),
						  "ERROR: The Owner is not Present on the MemberPage");
			
		// Test after importing of members are at Members page
		log.info("INFO: Validate that we are at members page after importing new members");
		Assert.assertEquals(driver.getTitle(), "Members - "+community.getName(), 
							"ERROR: After import operations we are not at Members page");
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
		}
	
	
	/**
	 * 
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part8: Filter & Sort By & Find a Member (1 of 5)</li>
	 *<li><B>Info:</B> Test Filter by options on Member Screen</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & add one Member to the community</li>
	 *<li><B>Step:</B> Login as owner</li>
	 *<li><B>Step:</B> Open community</li>
	 *<li><B>Step:</B> Navigate to members page</li> 
	 *<li><B>Verify:</B> The options present in Filter are All, Owners & Members</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF9A87921C4BF04085257C8D0072314D">TTT-MEMBERS PAGE PART8: FILTER & SORT BY & FIND A MEMBER</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void filterMemberOptions() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		String product = cfg.getProductName();
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.addMember(member)
													.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//log.info("INFO:Validation of member filter dropdown");
		log.info("INFO: Validate the member filter dropdown options");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MemberFilterBy).getText(), ui.getMemberFilterDropdown(community),
		"ERROR: The filter does not having correct data");		
				
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
		
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part8: Filter & Sort By & Find a Member (2 of 5)</li>
	 *<li><B>Info:</B> Test Owner Filter options</li>
	 *<li><B>Step:</B> Create a Public community as owner using API with two members (one owner, one member)</li>
	 *<li><B>Step:</B> Login as creator</li>
	 *<li><B>Step:</B> Open community</li> 
	 *<li><B>Step:</B> Add a new member to the community</li>
	 *<li><B>Step:</B> Add a new owner to the community</li>
	 *<li><B>Step:</B> Navigate to members page</li>
	 *<li><B>Step:</B> Select filter options select only Owners</li> 
	 *<li><B>Verify:</B> Validate our two Owners are present</li>
	 *<li><B>Verify:</B>Verify member user is not in the view when owner filter option is selected
	 **<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF9A87921C4BF04085257C8D0072314D">TTT-MEMBERS PAGE PART8: FILTER & SORT BY & FIND A MEMBER</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
		public void ownerFilterOption() throws Exception {

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			User owner1, owner2, member1, member2;
			
			owner1 = cfg.getUserAllocator().getUser();
			owner2 = cfg.getUserAllocator().getUser();
			member1 = cfg.getUserAllocator().getUser();
			member2 = cfg.getUserAllocator().getUser();
			
			
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Start Page for community " + testName)
														.addMember(new Member(CommunityRole.OWNERS, owner1))
														.addMember(new Member(CommunityRole.MEMBERS, member1))
														.build();

			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
					
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Go to Members page
			log.info("INFO: Select Members from left navigation Menu");		
			Community_LeftNav_Menu.MEMBERS.select(ui);
			
			log.info("INFO: Add an owner to community");		
			ui.addMemberCommunity(new Member(CommunityRole.OWNERS, owner2));
			
			//select save button
			log.info("INFO: Select save button");
			ui.clickSaveButton();
			
			log.info("INFO: Add Member to community");	
			ui.addMemberCommunity(new Member(CommunityRole.MEMBERS, member2));			
			
			//select save button
			log.info("INFO: Select save button");
			ui.clickSaveButton();
			
			// Select only Owners
			log.info("INFO: Select to sort by owners of community");
			driver.getSingleElement(CommunitiesUIConstants.MemberFilterBy).useAsDropdown().selectOptionByValue("owner");

			//Test owner is getting listed when we select owner in Filter
			log.info("INFO: Validate owner added during create is listed when sorted by owner");
			Assert.assertTrue(ui.isElementPresent("link=" + owner1.getDisplayName()),
							  "ERROR: Owner added when creating community not in view");

			//Test owner is getting listed when we select owner in Filter
			log.info("INFO: Vaildate owner added after creating the community is listed when sorted by Owner");
			Assert.assertTrue(ui.isElementPresent("link=" + owner2.getDisplayName()),
							  "ERROR: Owner added after creating the community not in view");
			
			//Test community creator is listed when we select owner in Filter
			log.info("INFO: Validate community creator is listed when sorted by owner");
			Assert.assertTrue(ui.isElementPresent("link=" + testUser.getDisplayName()),
							  "ERROR: Community creator is not in the view");
			
			//Test Member is not getting listed when we select owner in Filter
			log.info("INFO: Validate member added during create is not listed when sorted by owner");
			Assert.assertFalse(ui.isElementPresent("link=" + member1.getDisplayName()),
							  "ERROR: Member added when creating community is in the view");
			
			//Test Member is not getting listed when we select owner in Filter
			log.info("INFO: Validate member added after community create is not listed when sorted by owner");
			Assert.assertFalse(ui.isElementPresent("link=" + member2.getDisplayName()),
							  "ERROR: Member added when creating community is in the view");
						
			apiOwner.deleteCommunity(comAPI);

			ui.endTest();
		}
		
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part8: Filter & Sort By & Find a Member (3 of 5)</li>
	 *<li><B>Info:</B> Test Member Filter options</li>
	 *<li><B>Step:</B> Create a Public community as owner using API with two members (one owner, one member)</li>
	 *<li><B>Step:</B> Login as creator</li>
	 *<li><B>Step:</B> Open community</li> 
	 *<li><B>Step:</B> Add a new member to the community</li>
	 *<li><B>Step:</B> Add a new owner to the community</li>
	 *<li><B>Step:</B> Navigate to members page</li>
	 *<li><B>Step:</B> Select filter options select only Members</li> 
	 *<li><B>Verify:</B> Validate our two Members are present</li>
	 *<li><B>Verify:</B>Verify Owner user is not in the view when member filter option is selected
	 **<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF9A87921C4BF04085257C8D0072314D">TTT-MEMBERS PAGE PART8: FILTER & SORT BY & FIND A MEMBER</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
		public void memberFilterOption() throws Exception {

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			User owner1, owner2, member1, member2;
			
			owner1 = cfg.getUserAllocator().getUser();
			owner2 = cfg.getUserAllocator().getUser();
			member1 = cfg.getUserAllocator().getUser();
			member2 = cfg.getUserAllocator().getUser();
			
			
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Start Page for community " + testName)
														.addMember(new Member(CommunityRole.OWNERS, owner1))
														.addMember(new Member(CommunityRole.MEMBERS, member1))
														.build();

			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
					
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Go to Members page
			log.info("INFO: Select Members from left navigation Menu");		
			Community_LeftNav_Menu.MEMBERS.select(ui);
			
			log.info("INFO: Add an owner to community");		
			ui.addMemberCommunity(new Member(CommunityRole.OWNERS, owner2));
			
			//select save button
			log.info("INFO: Select save button");
			ui.clickSaveButton();
			
			log.info("INFO: Add Member to community");	
			ui.addMemberCommunity(new Member(CommunityRole.MEMBERS, member2));			
					
			//select save button
			log.info("INFO: Select save button");
			ui.clickSaveButton();
			
			log.info("INFO: Select to sort by members");
			driver.getSingleElement(CommunitiesUIConstants.MemberFilterBy).useAsDropdown().selectOptionByValue("member");
			
			//Test Member is getting listed when we select Members in Filter
			log.info("INFO: Validate member added when creating community is listed when sorted by Member");
			Assert.assertTrue(ui.isElementPresent("link=" + member1.getDisplayName()),
							  "ERROR: Member added when creating community not in view");

			//Test Member is getting listed when we select Members in Filter
			log.info("INFO: Validate member added after creating the community is listed when sorted by Member");
			Assert.assertTrue(ui.isElementPresent("link=" + member2.getDisplayName()),
							  "ERROR: Member added after creating the community not in view");
			
			//Test Owner is not getting listed when we select Members in Filter
			log.info("INFO: Validate owner added when creating community is not listed when sorted by Member");
			Assert.assertFalse(ui.isElementPresent("link=" + owner1.getDisplayName()),
							  "ERROR: Owner added when creating community is in the view");
			
			//Test Owner is not getting listed when we select Members in Filter
			log.info("INFO: Validate owner added after creating the community is not listed when sorted by Member");
			Assert.assertFalse(ui.isElementPresent("link=" + owner2.getDisplayName()),
							  "ERROR: Owner added after creating community is in the view");
			
			//Test community creator is not listed when we select Members in Filter
			log.info("INFO: Validate community creator is not listed when sorted by Member");
			Assert.assertFalse(ui.isElementPresent("link=" + testUser.getDisplayName()),
							  "ERROR: Community creator is in the view");

			apiOwner.deleteCommunity(comAPI);

			ui.endTest();
		}
	
	
	/**
	 * 
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part8: Filter & Sort By & Find a Member (4 of 5)</li>
	 *<li><B>Info:</B> Test Sort by options on Member Screen</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & add one Member to the community</li>
	 *<li><B>Step:</B> Login as owner</li>
	 *<li><B>Step:</B> Open community</li>
	 *<li><B>Step:</B> Navigate to members page</li> 
	 *<li><B>Verify:</B>Validate that sort by Name option is present</li>
	 *<li><B>Verify:</B>Validate that sort by date is present</li>
	 *<li><B>Verify:</B>verify that after clicking DateAdded link the members page updates to show users & groups who were added: oldest to newest and vice versa
	 *<li><B>Verify:</B>verify that after clicking Sort By Name link, list of users & groups get sorted into alphabetical order and viceversa
	 **<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF9A87921C4BF04085257C8D0072314D">TTT-MEMBERS PAGE PART8: FILTER & SORT BY & FIND A MEMBER</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void sortMemberOptions() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.addMember(new Member(CommunityRole.MEMBERS, testUser3))
													.addMember(new Member(CommunityRole.MEMBERS, testUser4))
													.addMember(new Member(CommunityRole.MEMBERS, testUser5))
													.addMember(new Member(CommunityRole.MEMBERS, testUser6))
													.build();
	
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);
		
		//Verify that the sort by date option is present and names of users are sorted date wise
		List<com.ibm.atmn.waffle.core.Element> Membersz = driver.getVisibleElements(CommunitiesUIConstants.getMemebers);
		String[] OrgData = ui.getnameFromMemberList(Membersz);
		
		log.info("INFO: Validate that the sort by date option is present");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.SortMembersByDate),
						  "ERROR: The Sort by Date option is not present ");
		
		//Verify list of users & groups sorted date wise ascending
		ui.clickLinkWait(CommunitiesUIConstants.SortMembersByDate);
		List<com.ibm.atmn.waffle.core.Element> MembersByDate = driver.getVisibleElements(CommunitiesUIConstants.getMemebers);
		log.info("INFO:Validate list of users & groups sorted date wise ascending ");
		ui.CheckReverse(OrgData, MembersByDate);
		
		//Verify users are sorted date wise descending
		ui.clickLinkWait(CommunitiesUIConstants.SortMembersByDate);
		MembersByDate = driver.getVisibleElements(CommunitiesUIConstants.getMemebers);
		log.info("INFO:Validate users are sorted date wise descending ");
		ui.CheckName(OrgData, MembersByDate);
		
		//Verify that the sort by name option is present and names are sorted into alphabetical order
		log.info("INFO: Validate that the sort by name option is present");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.SortMembersByName),
				         "ERROR: The Sort by name option is not present ");
		
        List<com.ibm.atmn.waffle.core.Element> Members = driver.getVisibleElements(CommunitiesUIConstants.getMemebers);
		
        //Verify list of users & groups get sorted into alphabetical order
		String[] sort = ui.getsortMemberList(Members);
		log.info("INFO: Validate list of users & groups get sorted into alphabetical order");
		ui.clickLinkWait(CommunitiesUIConstants.SortMembersByName);
		List<com.ibm.atmn.waffle.core.Element> MembersByName  = driver.getVisibleElements(CommunitiesUIConstants.getMemebers);
		ui.CheckName(sort, MembersByName);
        
		//Verify list of users & groups get sorted into alphabetical order, descending order
		log.info("INFO: Validate list of users & groups get sorted into alphabetical order, descending order");
		ui.clickLinkWait(CommunitiesUIConstants.SortMembersByName);
	    MembersByName  = driver.getVisibleElements(CommunitiesUIConstants.getMemebers);
		Arrays.sort(sort,Collections.reverseOrder());
		ui.CheckName(sort, MembersByName);
		
		apiOwner.deleteCommunity(comAPI);
						
		ui.endTest();
	}
	   
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part8: Filter & Sort By & Find a Member (5 of 5)</li>
	 *<li><B>Info:</B> Test find member options</li>
	 *<li><B>Step:</B> Create a Public community as owner using API with four members (two owner, two member)</li>
	 *<li><B>Step:</B> Login as creator</li>
	 *<li><B>Step:</B> Open community</li> 
	 *<li><B>Step:</B> Navigate to members page</li>
	 *<li><B>Step:</B> Select find member options</li> 
	 *<li><B>Verify:</B>Verify that only typed name displays in the members screen</li>
	 **<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF9A87921C4BF04085257C8D0072314D">TTT-MEMBERS PAGE PART8: FILTER & SORT BY & FIND A MEMBER</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
		public void findmemberOption() throws Exception {

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			User owner1, owner2, member1, member2;
			
			owner1 = cfg.getUserAllocator().getUser();
			owner2 = cfg.getUserAllocator().getUser();
			member1 = cfg.getUserAllocator().getUser();
			member2 = cfg.getUserAllocator().getUser();
			
			
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Start Page for community " + testName)
														.addMember(new Member(CommunityRole.OWNERS, owner1))
														.addMember(new Member(CommunityRole.MEMBERS, member1))
														.addMember(new Member(CommunityRole.OWNERS, owner2))
														.addMember(new Member(CommunityRole.MEMBERS, member2))
														.build();

			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
					
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Go to Members page
			log.info("INFO: Select Members from left navigation Menu");		
			Community_LeftNav_Menu.MEMBERS.select(ui);
	
	        //Go to find a member link
			log.info("INFO: Select find a member link from right side of the view all member screen");
			ui.clickLinkWait(CommunitiesUIConstants.FindAMember);
			
			//Type member name in Find member field
			log.info("INFO: Type member name in the field to find a member ");
			driver.getFirstElement(CommunitiesUIConstants.FindMembers);
			this.driver.getSingleElement(CommunitiesUIConstants.FindMembers).type(member1.getLastName());
			
			//Verify that only typed name displays in the members screen
			log.info("INFO: Validate only typed name or group displays in the members screen");
			Assert.assertFalse(ui.isElementPresent("link=" + owner1.getDisplayName()),
							  "ERROR: Owner added when creating community is in the view");

			//Verify that only typed name displays in the members screen
			log.info("INFO: Validate only typed name or group displays in the members screen");
			Assert.assertFalse(ui.isElementPresent("link=" + owner2.getDisplayName()),
							  "ERROR: Owner added after creating the community is in the view");
			
			//Verify that only typed name displays in the members screen
			log.info("INFO: Validate only typed name or group displays in the members screen");
			Assert.assertTrue(ui.isElementPresent("link=" + member1.getDisplayName()),
							  "ERROR: Member added when creating community is not in the view");
			
			//Verify that only typed name displays in the members screen
			log.info("INFO: Validate only typed name or group displays in the members screen");
			Assert.assertFalse(ui.isElementPresent("link=" + member2.getDisplayName()),
							  "ERROR: Member added when creating community is in the view");
			
			apiOwner.deleteCommunity(comAPI);
			
			ui.endTest();
			
	
}
	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Members Page Part6: Verify Edit Member Dialog (1 of 2)</li>
	 *<li><B>Info:</B> Edit user and validate that edit role option popup</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Go to members page</li>
	 *<li><B>Step:</B> Click on Edit link present under the member</li>
	 *<li><B>Verify:</B> Validate the Edit member screen area opens up</li>
	 *<li><B>Verify:</B> Validate the member radio button is present</li>
	 *<li><B>Verify:</B> Validate the owner radio button is present</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1031C08BEB41ACC985257C8D006C17B2">TTT-MEMBERS PAGE PART6: Verify Edit Member dialog</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void validateEditMemberForm() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
			
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.addMember(member)
													.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);
		
		//Click on the Edit link under Member's Name
		log.info("INFO: Click on the Edit link under Member's Name");
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);

		//Test Edit member screen opens up
		log.info("INFO: Edit member screen opens up");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MemberEditionPopUp).getText(), "Edit Member",
							"ERROR: After member edit we are not able to see the member edition screen");
		
		log.info("INFO: Find the edit member frame web element id");
		String editBoxID = ui.getMemberEditBoxId(member);

		log.info("INFO: Validate that the owner radio button is present");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUI.getMemberEditBoxOwner(editBoxID)),
						  "ERROR: Owner radio button is not present");
		
		log.info("INFO: Validate that the member radio button is present");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUI.getMemberEditBoxMember(editBoxID)),
						  "ERROR: Member radio button is not present");

		log.info("INFO: Select cancel button");
		ui.clickLinkWait(CommunitiesUIConstants.CancelButtonEditMemberScreen);
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();	
	}
		
	/**
	 *<ul>
	 *<li><B>Test Scenario: Members Page Part6: Verify Edit Member Dialog (2 of 2)</li> 
	 *<li><B>Info:</B> Verify the ability to change user access from Member to Owner </li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Go to members page</li>
	 *<li><B>Step:</B> Click on Edit link</li> 
	 *<li><B>Step:</B> Select Owner</li> 
	 *<li><B>Step:</B> Click on Cancel</li>
	 *<li><B>Verify:</B> Validate the Member type of member is still a Member</li>
	 *<li><B>Step:</B> Click on Edit link</li> 
	 *<li><B>Step:</B> Select Owner</li> 
	 *<li><B>Step:</B> Click on Save</li>
	 *<li><B>Verify:</B> Validate the Member type of member has now become owner</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1031C08BEB41ACC985257C8D006C17B2">TTT-MEMBERS PAGE PART6: Verify Edit Member dialog</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void changeRoleOwner() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Start Page for community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
				
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);
		
		//Click on the Edit link under Member's Name
		log.info("INFO: Editing the user details");
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);
		
		//hover on Business owner ? icon
		log.info("INFO Hover on ? icon next to Business owner to see the description");
		ui.businessOwnerDescription();
		
		//Click Owner radio button
		log.info("INFO: Select Owner radio button"); 
		ui.getFirstVisibleElement(CommunitiesUIConstants.OwnerRadioButton1).click();
		
		//Click on cancel button on Edit Member Screen
		log.info("INFO: Click on the cancel button present on the Edit Member screen");
		ui.clickLinkWait(CommunitiesUIConstants.CancelButtonEditMemberScreen);
		
		//Test that member type user is still a member
		log.info("INFO: Validate that the member type of user is still member"); 
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.MemberDetailArea).getText(), testUser1.getDisplayName()+"\nMember\nEdit | Remove",
							"ERROR: Member type is not equal to member");
		
		//Click on the Edit link under Member's Name
		log.info("INFO: Editing the user details");
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);
		
		//Click on Owner Radio button
		log.info("INFO Select Owner radio button:"); 
		ui.getFirstVisibleElement(CommunitiesUIConstants.OwnerRadioButton1).click();
								
		//Click on save button
		log.info("INFO: Select save button");
		ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
						
		//Test Member has become Owner now
		log.info("INFO: Validate that user is now an owner");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.MemberDetailArea).getText(), testUser1.getDisplayName()+"\nOwner\nEdit | Remove",
									"ERROR: The user did not become an owner");
		
		//Click on the Edit link under newly become Owner's Name
		log.info("INFO: Editing the Owner details");
		driver.getFirstElement(CommunitiesUIConstants.EditLink).click();
				
		//Click on Members Radio button
		log.info("INFO Select Member radio button:"); 
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberRadioButton).click();
				
		//Click on save button
		log.info("INFO: Select save button");
		ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
				
			
		//Test Owner has become Member now
		log.info("INFO: Validate that user is now an member");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.MemberDetailArea).getText(), testUser1.getDisplayName()+"\nMember\nEdit | Remove",
											"ERROR: The user did not become an Member");
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}

		
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Members Page Part9: Display users Profiles page & biz card from Members page</li>
	 *<li><B>Info:</B> That when selecting user name under member it takes to profile</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Login as owner</li> 
	 *<li><B>Step:</B> Open community</li>
	 *<li><B>Step:</B> Navigate to members page</li>
	 *<li><B>Step:</B> Select user name link</li>
	 *<li><B>Verify:</B> That you are redirected to owners page</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/738519F70368968D85257C8D00742AAC">TTT-MEMBERS PAGE PART9: DISPLAY USERS PROFILES PAGE & BIZ CARD FROM MEMBERS PAGE</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
		public void memberProfileLink() throws Exception {

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Start Page for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();

			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Go to Members page
			log.info("INFO: Select Members from left navigation Menu");		
			Community_LeftNav_Menu.MEMBERS.select(ui);
			
			ui.waitForPageLoaded(driver);
						
			//Click on owner to see his profile.
			log.info("INFO: Hover on owner to see profile");
			ui.viewBusinesscard(testUser);
			
			//Wait to load frame
			log.info("INFO: Wait to load frame");
			ui.frameEntry(hUI);
						
			//Verify member name and his biz card name is matching
			log.info("INFO: Test to verify member name and his biz card name is matching");
			Assert.assertEquals(testUser.getDisplayName(), ui.getbusinesscard(testUser),
			              "ERROR = Member name and his biz card name are not matching");
											
			log.info("INFO: Click on owner to see profile");
			ui.clickLinkWait("link="+testUser.getDisplayName());
			
			log.info("INFO: Test on the overview page the owner details are shown");
			Assert.assertTrue(ui.isElementPresent("link=" + testUser.getEmail()),
					          "ERRR:Overview page does not show owner details");
			
			log.info("INFO: Validate the name displayed matches expected");
			Assert.assertEquals(driver.getFirstElement("link="+testUser.getDisplayName()).getText(),testUser.getDisplayName(),
								"ERROR:User name does not match expected name.");
			
			apiOwner.deleteCommunity(comAPI);

			ui.endTest();
		}	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Panasonic UAT: Transfer Biz Ownership to another user</li>
	 *<li><B>Info:</B> Cloud only test.  On-prem does not currently have business owners.</li>
	 *<li><B>Step:</B> Create an internal restricted community using API with an additional member</li>
	 *<li><B>Step:</B> Login as owner</li> 
	 *<li><B>Step:</B> Open community & navigate to the Members page</li>
	 *<li><B>Step:</B> Click on the Edit link </li>
	 *<li><B>Step:</B> Click on the Business Owner radio button & Save change </li>
	 *<li><B>Step:</B> Click OK on the confirmation alert message </li>
	 *<li><B>Verify:</B> The member role is now Business Owner </li>
	 *<li><B>Verify:</B> The original business owner now has a role of Owner </li>
	 *<li><B>Step:</B> Cleanup: Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regressionCloud"})
	public void changeRoleToBusinessOwner() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		Member bizOwner = new Member(CommunityRole.OWNERS, testUser);

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                            .access(Access.RESTRICTED)	
		                                            .description("Change community member's role to Business Owner. ")	
		                                            .addMember(member)
		                                            .rbl(false)
		                                            .shareOutside(false)
		                                            .build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Go to Members page
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		//Click on the Edit link under the selected member's name
		log.info("INFO: Editing the user details");
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);
		
		//Verify the Business Owner radio button displays
		//NOTE: The HTML for the Edit Member dialog shows (2) Business Owner radio buttons.  HTML for the 1st button is hidden, the 2nd button is visible.
		//The following step verifies that there are (2)Business Owner radio buttons. 
		log.info("INFO: Verify the Business Owner radio button appears on the Edit Member pop-up");
		List<Element> radiobuttons=driver.getElements("css=input[id^='businessownerradioid'][type='radio']");
		Assert.assertTrue((radiobuttons).size()==2,
				"ERROR: The Business Owner radio button does not display on the Edit Member pop-up");

		//Click on the Business Owner Radio button 
		//As mentioned above, the first Business Owner radio button is hidden.  This step will click on the 2nd button listed which is the visible one.
		//The first radio button is (0), the visible radio button is (1), so we get radio button (1) and click on it.
		log.info("INFO: Click on the Business Owner radio button"); 
		radiobuttons.get(1).click();
		
		//Click on the Save button on the Edit Member pop-up
		log.info("INFO: Click on the Edit Member pop-up Save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditMemberDialogSaveButton).click();
		
		//Click on the OK button on the change business owner confirmation alert message
		log.info("INFO: Click on the OK button on the change business owner confirmation alert message");
		Alert changeBizOwnerAlert=driver.switchToAlert();
		changeBizOwnerAlert.accept();	
		
		//Verify the community member is now the Business Owner
		log.info("INFO: Collect the members text from member page");
		String memberInfo = ui.getMemberElement(member).getText();
			
		log.info("INFO: Verify the community member is now the Business Owner");
		Assert.assertTrue(memberInfo.contains("Business Owner"),
						 "ERROR: The original Member is not listed as the Business Owner");	
			
		//Verify the original business owner is now an Owner
		log.info("INFO: Collect the members text from member page");
		String ownerInfo = ui.getMemberElement(bizOwner).getText();
		
		log.info("INFO: Verify the original Business Owner is now an Owner");
		Assert.assertTrue(ownerInfo.contains("Owner"),
				  "ERROR: The original Business Owner is not listed as the Owner");
												
		//Cleanup: Delete the community created in this test case
		log.info("INFO: Cleanup: Removing community for Test case " + testName );
		community.delete(ui, testUser);
		
		ui.endTest();
		
			}
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test will change the user role from Member to Owner</li>
	 * <li><B>Step:</B> Create a public community with an additional owner & member using the API </li>
	 * <li><B>Step:</B> Log into Communities as the user with Member access</li>
	 * <li><B>Step:</B> Open the community and click on the Community Actions link</li>
	 * <li><B>Verify:</B> The Member should not see the Edit Community or Create Subcommunity action links</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The Member should not see Add Members button, Edit or Remove links</li>
	 * <li><B>Step:</B> Log out as the Member and log in as the additional Owner </li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Step:</B> Change the role for the user with Member access to be Owner access</li>
	 * <li><B>Step:</B> Log out as the additional owner and log in as the user with the new role of Owner</li>
	 * <li><B>Step:</B> Open the community and click on the Community Actions link</li>
	 * <li><B>Verify:</B> The new Owner sees the Edit Community and Create Subcommunity action links</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The user sees the Add Members button, Edit and Remove links</li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressionCloud"})
	public void changeRoleFromMemberToOwner() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member owner = new Member(CommunityRole.OWNERS, testUser1);
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		String product = cfg.getProductName();

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .description("Change user's role from Member to Owner. ")	
		                                            .addMember(owner)
		                                            .addMember(member)
		                                            .rbl(false)
		                                            .shareOutside(false)
		                                            .build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Load component and login as the user with Member access
		log.info("INFO: Log into Communities as the user with Member access " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		//list of community owners & members
		log.info("INFO: The community creator is: " + testUser.getDisplayName());
		log.info("INFO: The additional Owner is: " + testUser1.getDisplayName());
		log.info("INFO: The additional Member is: " + testUser2.getDisplayName());
		
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		
		//Click on the Community Actions link
		log.info("INFO: Click on Community Actions");
		ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);
		
		driver.turnOffImplicitWaits();
		
		//Verify the action 'Edit Community' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Edit Community' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.EDIT.getMenuItemText()),
				"ERROR: The Edit Community link should NOT appear on the drop-down menu, but it does");
		
		//Verify the action 'Create Subcommunity' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Create Subcommunity' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.CREATESUB.getMenuItemLink()),
				"ERROR: The Create Subcommunity link should NOT appear on the drop-down menu, but it does");
		
		//Verify the action 'Add Apps' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Add Apps' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.ADDAPP.getMenuItemText()),
				"ERROR: The Add Apps link should NOT appear on the drop-down menu, but it does");

		//Verify the action 'Change Layout' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Change Layout' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.CHANGELAYOUT.getMenuItemText()),
				"ERROR: The Change Layout link should NOT appear on the drop-down menu, but it does");

		//Verify the action 'Move Community' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Move Community' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.MOVECOMMUNITY.getMenuItemText()),
				"ERROR: The Move Community link should NOT appear on the drop-down menu, but it does");

		//Verify the action 'Delete Community' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Delete Community' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.DELETE.getMenuItemText()),
				"ERROR: The Delete Community link should NOT appear on the drop-down menu, but it does");

		driver.turnOnImplicitWaits();
		
		//Verify the action 'Mail Community' IS listed on the drop-down menu
		log.info("INFO: Verify the action 'Mail Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.MAILCOMMUNITY.getMenuItemText()),
				"ERROR: The Mail Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Leave Community' IS listed on the drop-down menu
		log.info("INFO: Verify the action 'Leave Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.LEAVE.getMenuItemText()),
				"ERROR: The Leave Community link should appear on the drop-down menu, but does not");
		
		//Navigate to the Members page
		log.info("INFO: Navigate to the Members full widget page");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		driver.turnOffImplicitWaits();
		
		//Verify the Member does NOT see the 'Add Members' button on the full Members page
		log.info("INFO: Verify the Member, " + testUser2.getDisplayName() + " ,does NOT see the 'Add Members' button on the full Members page");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.AddMembersToExistingCommunity),
				"ERROR: The Add Members button appears on the full Members widget page, but should not");
			
		//Verify the Member does NOT see the 'Edit' links on the full Members page
		log.info("INFO: Verify the Member, " + testUser2.getDisplayName() + " ,does NOT see any 'Edit' links on the full Members page");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.EditLink),
				"ERROR: The Edit link appears on the full Members page for the Member, but should not");

		//Verify the Member does NOT see the 'Remove' link on the full Members page
		log.info("INFO: Verify the Member, " + testUser2.getDisplayName() + " ,does NOT see any 'Remove' links on the full Members page");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.RemoveMemberLink),
				"ERROR: The Remove link appears on the full Members page for the Owner, but should not");	
		
		driver.turnOnImplicitWaits();
		
		//Logout as the member
		log.info("INFO: Log out as the member " + testUser2.getDisplayName());
		ui.logout();
		
		//Load component and login as the additional Owner
		log.info("INFO: log in as the additional owner " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser1);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Navigate to the Members page
		log.info("INFO: Navigate to the Members full widget page");
		Community_LeftNav_Menu.MEMBERS.select(ui);
			
		//Click on the Edit link for the additional member
		log.info("INFO: Click on the Edit link for the additional member");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditLink).click();
		
		//Select the radio button 'Owner'
		log.info("INFO: Select the radio button option 'Owner'");
		ui.getFirstVisibleElement(CommunitiesUIConstants.OwnerRadioButton1).click();
		
		//Click on save button
		log.info("INFO: Select save button");
		ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
			
		//logout as the additional Owner
		log.info("INFO: Log out as the additional Owner " + testUser2.getDisplayName());
		ui.logout();
		
		//Load component and login as the user whose role is changed from member to owner
		log.info("INFO: Log in as the user whose role changed from Member to Owner " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser2);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Click on the Community Actions link
		log.info("INFO: Click on Community Actions");
		ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);
		
		//Verify the action 'Edit Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Edit Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.EDIT.getMenuItemText()),
				"ERROR: The Edit Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Create Subcommunity' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Create Subcommunity' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.CREATESUB.getMenuItemText()),
				"ERROR: The Create Subcommunity link should appear on the drop-down menu, but does not");
		
		//Verify the action 'Add Apps' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Add Apps' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.ADDAPP.getMenuItemText()),
				"ERROR: The Add Apps link should appear on the drop-down menu, but does not");

		//Verify the action 'Change Layout' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Change Layout' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.CHANGELAYOUT.getMenuItemText()),
				"ERROR: The Change Layout link should appear on the drop-down menu, but does not");

		//Verify the action 'Move Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Move Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.MOVECOMMUNITY.getMenuItemText()),
				"ERROR: The Move Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Delete Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Delete Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.DELETE.getMenuItemText()),
				"ERROR: The Delete Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Mail Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Mail Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.MAILCOMMUNITY.getMenuItemText()),
				"ERROR: The Mail Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Leave Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Leave Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.LEAVE.getMenuItemText()),
				"ERROR: The Leave Community link should appear on the drop-down menu, but does not");

		//Navigate to the Members full widget page
		log.info("INFO: Navigate to the Members full widget page");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Verify the new Owner sees the 'Add Members' button on the full Members page
		log.info("INFO: Verify the new Owner, " + testUser2.getDisplayName() + " ,sees the 'Add Members' button on the full Members page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddMembersToExistingCommunity),
				"ERROR: The Add Members button should appear on the full Members widget page, but does not");

		//determine of SC or OP
		if(product.equalsIgnoreCase("cloud")){
			//Verify the new Owner sees 'Edit' links on the Members full widget page
			//On the cloud the additional owner will not see an Edit or Remove link for the Business Owner
			log.info("INFO: Verify the new Owner, " + testUser2.getDisplayName() + " ,sees 'Edit' links on the Members full widget page");
			List<Element> editLinksAsMember=driver.getElements(CommunitiesUIConstants.EditLink);
			Assert.assertTrue((editLinksAsMember).size()==2,
					"ERROR: The Edit link should appear on the full Members widget page, but does not");

		}else{
			//Verify the new Owner sees 'Edit' links on the Members full widget page
			//On-prem there is no 'Business Owner'.  Owners will see Edit & Remove links for other owners & members
			log.info("INFO: Verify the new Owner, " + testUser2.getDisplayName() + " ,sees 'Edit' links on the Members full widget page");
			List<Element> editLinksAsMember=driver.getElements(CommunitiesUIConstants.EditLink);
			Assert.assertTrue((editLinksAsMember).size()==3,
					"ERROR: The Edit link should appear on the full Members widget page, but does not");

		}				

		//determine of SC or OP
		if(product.equalsIgnoreCase("cloud")){
			//Verify the new Owner sees 'Remove' links on the Members full widget page
			//On the cloud the additional owner will not see an Edit or Remove link for the Business Owner
			log.info("INFO: Verify the new Owner, " + testUser2.getDisplayName() + " ,sees 'Remove' links on the Members full widget page");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.RemoveMemberLink),
					"ERROR: The Remove link should appear on the full Members widget page, but does not");


		}else{
			//Verify the new Owner sees 'Remove' links on the Members full widget page
			//On-prem there is no 'Business Owner'.  Owners will see Edit & Remove links for other owners & members
			log.info("INFO: Verify the new Owner, " + testUser2.getDisplayName() + " ,sees 'Remove' links on the Members full widget page");
			List<Element> editLinksAsMember=driver.getElements(CommunitiesUIConstants.RemoveMemberLink);
			Assert.assertTrue((editLinksAsMember).size()==2,
					"ERROR: The Remove link should appear on the full Members widget page, but does not");

		}	

		//Delete the community created in this test case
		log.info("INFO: Cleanup: Removing community for Test case " + testName );
		community.delete(ui, testUser2);
		
		ui.endTest();
		
			}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test will change the user role from Owner to Member</li>
	 * <li><B>Step:</B> Create a public community with an additional owner & member using the API </li>
	 * <li><B>Step:</B> Log into Communities as the additional Owner</li>
	 * <li><B>Step:</B> Open the community and click on the Community Actions link</li>
	 * <li><B>Verify:</B> The additional Owner should see the Edit Community or Create Subcommunity action links</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The additional Owner should see Add Members button, Edit and Remove links</li>
	 * <li><B>Step:</B> Log out as the additional owner and log in as the community creator </li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Step:</B> Change the role for the additional owner to be Member access</li>
	 * <li><B>Step:</B> Log out as the community creator and log in as the user who now has Member</li>
	 * <li><B>Step:</B> Open the community and click on the Community Actions link</li>
	 * <li><B>Verify:</B> The new Member does not see the Edit Community or Create Subcommunity action links</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The user does not see the Add Members button, Edit or Remove links</li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressionCloud"})
	public void changeRoleFromOwnerToMember() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member owner = new Member(CommunityRole.OWNERS, testUser1);
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		String product = cfg.getProductName();

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .description("Change user's role from Owner to Member. ")	
		                                            .addMember(owner)
		                                            .addMember(member)
		                                            .rbl(false)
		                                            .shareOutside(false)
		                                            .build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);	

		//Load component and login as the additional Owner
		log.info("INFO: Log into Communities as the additional Owner " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//list of community owners & members
		log.info("INFO: The community creator is: " + testUser.getDisplayName());
		log.info("INFO: The additional Owner is: " + testUser1.getDisplayName());
		log.info("INFO: The additional Member is: " + testUser2.getDisplayName());

		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		
		//Click on the Community Actions link
		log.info("INFO: Click on Community Actions");
		ui.clickLinkWithJavascript(BaseUIConstants.Community_Actions_Button);
		
		//Verify the action 'Edit Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Edit Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.EDIT.getMenuItemText()),
				"ERROR: The Edit Community link does not appear on the drop-down menu");
		
		//Verify the action 'Create Subcommunity' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Create Subcommunity' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.CREATESUB.getMenuItemText()),
				"ERROR: The Create Subcommunity link does not appear on the drop-down menu");
		
		//Verify the action 'Add Apps' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Add Apps' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.ADDAPP.getMenuItemText()),
				"ERROR: The Add Apps link should appear on the drop-down menu, but does not");

		//Verify the action 'Change Layout' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Change Layout' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.CHANGELAYOUT.getMenuItemText()),
				"ERROR: The Change Layout link should appear on the drop-down menu, but does not");

		//Verify the action 'Move Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Move Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.MOVECOMMUNITY.getMenuItemText()),
				"ERROR: The Move Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Delete Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Delete Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.DELETE.getMenuItemText()),
				"ERROR: The Delete Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Mail Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Mail Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.MAILCOMMUNITY.getMenuItemText()),
				"ERROR: The Mail Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Leave Community' is listed on the drop-down menu
		log.info("INFO: Verify the action 'Leave Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.LEAVE.getMenuItemText()),
				"ERROR: The Leave Community link should appear on the drop-down menu, but does not");
		
		//Navigate to the Members page
		log.info("INFO: Navigate to the Members full widget page");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Verify the additional Owner sees the 'Add Members' button on the full Members page
		log.info("INFO: Verify the additional Owner, " + testUser1.getDisplayName() + " ,sees the 'Add Members' button on the full Members page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddMembersToExistingCommunity),
				"ERROR: The Add Members button does not appear on the full Members widget page");
			
		//determine of SC or OP
			if(product.equalsIgnoreCase("cloud")){
				//Verify the new Owner sees 'Edit' links on the Members full widget page
				//On the cloud the additional owner will not see an Edit or Remove link for the Business Owner
				log.info("INFO: Verify the additional Owner, " + testUser1.getDisplayName() + " ,sees 'Edit' links on the Members full widget page");
				List<Element> editLinksAsMember=driver.getElements(CommunitiesUIConstants.EditLink);
				Assert.assertTrue((editLinksAsMember).size()==2,
						"ERROR: The Edit link should appear on the full Members widget page, but does not");

			}else{
				//Verify the new Owner sees 'Edit' links on the Members full widget page
				//On-prem there is no 'Business Owner'.  Owners will see Edit & Remove links for other owners & members
				log.info("INFO: Verify the additional Owner, " + testUser1.getDisplayName() + " ,sees 'Edit' links on the Members full widget page");
				List<Element> editLinksAsMember=driver.getElements(CommunitiesUIConstants.EditLink);
				Assert.assertTrue((editLinksAsMember).size()==3,
						"ERROR: The Edit link should appear on the full Members widget page, but does not");

			}				

			//determine of SC or OP
			if(product.equalsIgnoreCase("cloud")){
				//Verify the new Owner sees 'Remove' links on the Members full widget page
				//On the cloud the additional owner will not see an Edit or Remove link for the Business Owner
				log.info("INFO: Verify the additional Owner, " + testUser1.getDisplayName() + " ,sees 'Remove' links on the Members full widget page");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.RemoveMemberLink),
						"ERROR: The Remove link should appear on the full Members widget page, but does not");


			}else{
				//Verify the new Owner sees 'Remove' links on the Members full widget page
				//On-prem there is no 'Business Owner'.  Owners will see Edit & Remove links for other owners & members
				log.info("INFO: Verify the additional Owner, " + testUser1.getDisplayName() + " ,sees 'Remove' links on the Members full widget page");
				List<Element> removeLinksAsMember=driver.getElements(CommunitiesUIConstants.RemoveMemberLink);
				Assert.assertTrue((removeLinksAsMember).size()==2,
						"ERROR: The Remove link should appear on the full Members widget page, but does not");

			}	
		
		//Remove the additional Member from the community
		log.info("INFO: Remove the additional member from the community");
		//ui.clickLinkWait(CommunitiesUI.RemoveMemberLink);
		ui.getFirstVisibleElement(CommunitiesUIConstants.RemoveMemberLink).click();
		
		//Click OK on the Remove member confirmation pop-up
		log.info("INFO: Click OK on the Remove Members pop-up dialog");
		ui.clickLinkWait(CommunitiesUIConstants.RemoveMembersOKBtn);
		
		//Logout as the additional owner
		log.info("INFO: Log out as the additional Owner " + testUser1.getDisplayName());
		ui.logout();
		
		//Load component and login as the community creator
		log.info("INFO: log in as the community creator " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Navigate to the Members page
		log.info("INFO: Navigate to the Members full widget page");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Click on the Edit link for the additional owner
		log.info("INFO: Click on the Edit link for the additional owner");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditLink).click();
		
		//Select the radio button 'Member'
		log.info("INFO: Select the radio button option 'Member'");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberRadioButton).click();
		
		//Click on save button
		log.info("INFO: Select save button");
		ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
		
		log.info("INFO: Add Member to community");		
		ui.addMemberCommunity(member);
		
		//select save button
		log.info("INFO: Select save button");
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);
		
		//logout as the community creator
		log.info("INFO: Log out as the community creator " + testUser.getDisplayName());
		ui.logout();
		
		//Load component and login as the user with Member access
		log.info("INFO: Log in as the user with the new role of 'Member' " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Click on the Community Actions link
		log.info("INFO: Click on Community Actions");
		ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);
		
		driver.turnOffImplicitWaits();
		
		//Verify the action 'Edit Community' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Edit Community' is NOT listed on the drop-down menu for the member");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.EDIT.getMenuItemText()),
				"ERROR: The Edit Community link should NOT appear on the drop-down menu for the user with Member access");

		//Verify the action 'Create Subcommunity' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Create Subcommunity' is NOT listed on the drop-down menu for the member");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.CREATESUB.getMenuItemText()),
				"ERROR: The Create Subcommunity link should NOT appear on the drop-down menu for the user with Member access");
		
		//Verify the action 'Add Apps' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Add Apps' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.ADDAPP.getMenuItemText()),
				"ERROR: The Add Apps link should NOT appear on the drop-down menu, but it does");

		//Verify the action 'Change Layout' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Change Layout' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.CHANGELAYOUT.getMenuItemText()),
				"ERROR: The Change Layout link should NOT appear on the drop-down menu, but it does");

		//Verify the action 'Move Community' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Move Community' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.MOVECOMMUNITY.getMenuItemText()),
				"ERROR: The Move Community link should NOT appear on the drop-down menu, but it does");

		//Verify the action 'Delete Community' is NOT listed on the drop-down menu
		log.info("INFO: Verify the action 'Delete Community' is NOT listed on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.DELETE.getMenuItemText()),
				"ERROR: The Delete Community link should NOT appear on the drop-down menu, but it does");
		
		driver.turnOnImplicitWaits();

		//Verify the action 'Mail Community' IS listed on the drop-down menu
		log.info("INFO: Verify the action 'Mail Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.MAILCOMMUNITY.getMenuItemText()),
				"ERROR: The Mail Community link should appear on the drop-down menu, but does not");

		//Verify the action 'Leave Community' IS listed on the drop-down menu
		log.info("INFO: Verify the action 'Leave Community' is listed on the drop-down menu");
		Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.LEAVE.getMenuItemText()),
				"ERROR: The Leave Community link should appear on the drop-down menu, but does not");

		//Navigate to the Members full widget page
		log.info("INFO: Navigate to the Members full widget page");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		driver.turnOffImplicitWaits();
		
		//Verify the Member does not see the 'Add Members' button on the full Members page
		log.info("INFO: Verify the user with Member access, " + testUser1.getDisplayName() + " ,does not see the 'Add Members' button on the full Members page");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.AddMembersToExistingCommunity),
				"ERROR: The Add Members button appears on the full Members widget page for the user with Member access");

		//Verify the user with Member access does not see any 'Edit' links on the Members full widget page
		log.info("INFO: Verify the user with Member access, " + testUser1.getDisplayName() + " ,does not see any 'Edit' links on the Members full widget page");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.EditLink),
				"ERROR: The Edit link appears on the full Members page for the Member, but should not");

		//Verify the user with Member access does not see any 'Remove' links on the Members full widget page
		log.info("INFO: Verify the user with Member access, " + testUser1.getDisplayName() + " ,does not see any 'Remove' links on the Members full widget page");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.RemoveMemberLink),
				"ERROR: The Remove link appears on the full Members page for the user with Member access");
		
		driver.turnOnImplicitWaits();
		
		//logout as the user with Member access
		log.info("INFO: Log out as the user with the new role of 'Member' " + testUser1.getDisplayName());
		ui.logout();

		//Load component and login as the community creator
		log.info("INFO: Log in as the community creator " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
													
		//Delete the community created in this test case
		log.info("INFO: Cleanup:Removing community for Test case " + testName );
		community.delete(ui, testUser);
		
		ui.endTest();
		
			}


	
	}




