package com.ibm.conn.auto.tests.communities.regression;

import java.util.List;
import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.Executor.Alert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.userBuilder.UserSelector;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityOrgAdmin extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(CommunityOrgAdmin.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User adminUser,testUser1,testUser2,testUser3;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private Community comAPI1,comAPI2,comAPI3,comAPI4,comAPI5,comAPI6,comAPI7,comAPI8,comAPI9;
	private BaseCommunity community1,community2,community3,community4,community5,community6,community7,community8,community9;	
		

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		boolean adminfound = false;
		cfg = TestConfigCustom.getInstance();
				
		//Load Users
		String userToken = getClass().getSimpleName() + Helper.genStrongRand();
		ArrayList<User> listOfTestUsers = UserSelector.selectUniqueUsers_Standard(cfg, userToken, 3);
		testUser1 = listOfTestUsers.get(0);
		testUser2 = listOfTestUsers.get(1);
		testUser3 = listOfTestUsers.get(2);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		
		//NOTE: This 'for' loop is to get an admin user that has a Connections subscription
		//If there is no admin user with a Connections subscription no tests will be run
		for (int x=0; x<5; x++){
			adminUser = UserSelector.selectUniqueUsers_Admin(cfg, userToken, 1).get(0);
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(adminUser);
			log.info("INFO: Attempt number "+x+ " to see if an Access Denied message appears when admin user " + adminUser.getDisplayName() + " logs into Connections");
			if (!driver.isTextPresent(Data.getData().accessDenied))
			{adminfound=true;
			log.info("INFO: Admin user with Connections subscription found");
			ui.logout();
			ui.close(cfg);
			break;
			}
			ui.logout();
			ui.close(cfg);

		}
		log.info("INFO: Verify that an admin user with Connections subscription is found");
		Assert.assertTrue(adminfound,
				"ERROR: No admin user with a Connections subscription was found");

		log.info("INFO: Admin user with Connections subsription is " + adminUser.getDisplayName());

				
		//Test communities:		
		community1 = new BaseCommunity.Builder("orgAdminAddMemberChangeBizOwner" + Helper.genDateBasedRandVal())
		                              .access(Access.PUBLIC)
		                              .description("org Admin adds a member and makes them the biz owner.")
		                              .build();
				
		community2 = new BaseCommunity.Builder("orgAdminNonMemberPublicComm" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("org Admin who is non-member of the public community can see buttons & links on the Members page.")
                                      .build();
		
		community3 = new BaseCommunity.Builder("orgAdminNonMemberModeratedComm" + Helper.genDateBasedRandVal())
                                      .access(Access.MODERATED)
                                      .description("org Admin who is a non-member of the moderated community can see buttons & links on the Members page.")
                                      .build();
		
		community4 = new BaseCommunity.Builder("orgAdminNonMemberInternalComm" + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("org Admin who is a non-member of the internal community CANNOT see buttons & links on the Members page.")
                                      .build();
						
		community5 = new BaseCommunity.Builder("orgAdminInviteMemberResendAndRevoke" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("org Admin invites a member and then does a Resend/Revoke of the invitation.")
                                      .build();
		
		community6 = new BaseCommunity.Builder("orgAdminImportMember" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("org Admin imports members test")
                                      .build();
		
		community7 = new BaseCommunity.Builder("orgAdminChangeOwnerToMember" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("org Admin - change user access level from Owner to Member")
                                      .build();
		
		community8 = new BaseCommunity.Builder("orgAdminChangeMemberToOwner" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("org Admin - change user access level from Member to Owner")
                                      .build();
		
		community9 = new BaseCommunity.Builder("orgAdminInternalCommChangeMemberToBizOwner" + Helper.genDateBasedRandVal())
                                       .access(Access.RESTRICTED)
                                       .description("org Admin adds a member and makes them the biz owner - internal restricted community.")
                                       .build();
			
		
		log.info("INFO: create communities via the API");
		comAPI1 = community1.createAPI(apiOwner);
		comAPI2 = community2.createAPI(apiOwner);
		comAPI3 = community3.createAPI(apiOwner);
		comAPI4 = community4.createAPI(apiOwner);
		comAPI5 = community5.createAPI(apiOwner);
		comAPI6 = community6.createAPI(apiOwner);
		comAPI7 = community7.createAPI(apiOwner);
		comAPI8 = community8.createAPI(apiOwner);
		comAPI9 = community9.createAPI(apiOwner);
				
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {
		
		log.info("INFO: Cleanup - delete communities");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);
		apiOwner.deleteCommunity(comAPI3);
		apiOwner.deleteCommunity(comAPI4);
		apiOwner.deleteCommunity(comAPI5);
		apiOwner.deleteCommunity(comAPI6);
		apiOwner.deleteCommunity(comAPI7);
		apiOwner.deleteCommunity(comAPI8);
		apiOwner.deleteCommunity(comAPI9);
		
	}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Org Admin (non-member)can add a Member to the community and then make the member the business owner </li>
	 * <li><B>Info:</B> This test will verify an Org Admin who is not a member of the community can add a member & make the member the business owner</li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Step:</B> Add an internal user to the community with 'member' access</li>
	 * <li><B>Step:</B> Edit the member access and make them the business owner</li>
	 * <li><B>Verify:</B> The user with Member access is now the business owner</li>
	 * <li><B>Verify:</B> The original business owner now has Owner access</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminAddMemberChangeBizOwner(){

		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member bizOwner = new Member(CommunityRole.OWNERS, testUser1);


		log.info("INFO: Get the UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Add Member to community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Validate that the user appears in blue box below the add fields to be added to a community");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.selectedUserToAddToComm),
				"ERROR: Members blue box is not present");

		log.info("INFO: Select save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberSaveButton).click();

		log.info("INFO: Click on the Edit link under the member named: " + member.getUser().getDisplayName());
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);
		
		log.info("INFO: Click on the Business Owner radio button");
		selectBusinessOwnerRadioButton();
		
		log.info("INFO: Click on the Edit Member pop-up Save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditMemberDialogSaveButton).click();

		log.info("INFO: Click on the OK button on the change business owner confirmation alert message");
		Alert changeBizOwnerAlert=driver.switchToAlert();
		changeBizOwnerAlert.accept();	

		log.info("INFO: Get the access level for: " + member.getUser().getDisplayName());
		String memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Verify the community member " + member.getUser().getDisplayName() + " is now the Business Owner");
		Assert.assertTrue(memberInfo.contains(Data.getData().businessOwnerRole),
				"ERROR: The original Member is not listed as the Business Owner");	

		log.info("INFO: Get the access level for: " + bizOwner.getUser().getDisplayName());
		String ownerInfo = ui.getMemberElement(bizOwner).getText();

		log.info("INFO: Verify the original Business Owner " + bizOwner.getUser().getDisplayName() + "is now an Owner");
		Assert.assertTrue(ownerInfo.contains(Data.getData().ownerRole),
				"ERROR: The original Business Owner is not listed as the Owner");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Org Admin (non-member)- Public Community </li>
	 * <li><B>Info:</B> This test will verify an Org Admin who is not a member of a Public community can see the Add/Invite/Import/Export buttons</li>
	 * <li><B>Info:</B> The test will also verify the Org Admin can see the Edit/Remove link for a member</li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The Org Admin can see the Add, Invite, Import and Export buttons</li>
	 * <li><B>Verify:</B> The Org Admin does not see Edit/Remove link for business owner</li>
	 * <li><B>Step:</B> Org Admin adds a member to the community</li>
	 * <li><B>Verify:</B> Org Admin sees Edit/Remove link for added member</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminNonMemberPublicComm(){

		Member member = new Member(CommunityRole.MEMBERS, testUser2);		    		    	

		log.info("INFO: Get the UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community2.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Make sure each of these buttons appear on the full Members page: Add Members, Invite Members, Import Members, Export Members");
		verifyButtonsOnMembersPage();

		log.info("INFO: Verify the number of Edit links on the page is 0");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.EditLink).size(),0,
				"ERROR: Number of Edit links does not equal 0");

		log.info("INFO: Verify the number of Remove links on the page is 0");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.RemoveMemberLink).size(),0,
				"ERROR: Number of Remove links does not equal 0");

		log.info("INFO: Add Member to community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Select save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberSaveButton).click();

		log.info("INFO: Verify the number of Edit links on the page is 1");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.EditLink).size(),1,
				"ERROR: Number of Edit links does not equal 1");

		log.info("INFO: Verify the number of Remove links on the page is 1");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.RemoveMemberLink).size(),1,
				"ERROR: Number of Remove links does not equal 1");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Org Admin (non-member)- Moderated Community </li>
	 * <li><B>Info:</B> This test will verify an Org Admin who is not a member of a Moderated community can see the Add/Invite/Import/Export buttons</li>
	 * <li><B>Info:</B> The test will also verify the Org Admin can see the Edit/Remove link for a member</li>
	 * <li><B>Step:</B> Create a moderated community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The Org Admin can see the Add, Invite, Import and Export buttons</li>
	 * <li><B>Verify:</B> The Org Admin does not see Edit/Remove link for business owner</li>
	 * <li><B>Step:</B> Org Admin adds a member to the community</li>
	 * <li><B>Verify:</B> Org Admin sees Edit/Remove link for added member</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminNonMemberModeratedComm(){

		Member member = new Member(CommunityRole.MEMBERS, testUser2);

		log.info("INFO: Get the UUID of community");
		community3.getCommunityUUID_API(apiOwner, comAPI3);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community3.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Make sure each of these buttons appear on the full Members page: Add Members, Invite Members, Import Members, Export Members");
		verifyButtonsOnMembersPage();

		log.info("INFO: Verify the number of Edit links on the page is 0");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.EditLink).size(),0,
				"ERROR: Number of Edit links does not equal 0");

		log.info("INFO: Verify the number of Remove links on the page is 0");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.RemoveMemberLink).size(),0,
				"ERROR: Number of Remove links does not equal 0");

		log.info("INFO: Add Member to community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Select save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberSaveButton).click();

		log.info("INFO: Verify the number of Edit links on the page is 1");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.EditLink).size(),1,
				"ERROR: Number of Edit links does not equal 1");

		log.info("INFO: Verify the number of Remove links on the page is 1");
		Assert.assertEquals(driver.getElements(CommunitiesUIConstants.RemoveMemberLink).size(),1,
				"ERROR: Number of Remove links does not equal 1");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Org Admin (non-member)- Internal Community </li>
	 * <li><B>Info:</B> This test will verify an Org Admin who is not a member of the internal community does not have access to the community</li>
	 * <li><B>Step:</B> Create an internal community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Access the community via the community URL</li>
	 * <li><B>Verify:</B> An Access Denied error message displays</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminNonMemberInternalComm() {


		log.info("INFO: Get the UUID of community");
		community4.getCommunityUUID_API(apiOwner, comAPI4);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community4.navViaUUID(ui);

		log.info("INFO: Verify an Access denied message displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().accessDenied),
				"ERROR: No Access denied error message displays");
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Org Admin (non-member)- Invite Member, Resend & Revoke </li>
	 * <li><B>Info:</B> This test will verify an Org Admin who is not a member of a community can invite a member and Resend/Revoke the invite</li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The Org Admin can see the Add, Invite, Import and Export buttons</li>
	 * <li><B>Step:</B> Org Admin invites a member to the community</li>
	 * <li><B>Step:</B> Click on the Invitations tab</li>
	 * <li><B>Step:</B>) Click on the Resend link</li>
	 * <li><B>Verify:</B> Org Admin sees the message that the user was successfully invited to the community. </li>
	 * <li><B>Step:</B>) Click on the Revoke link</li>
	 * <li><B>Verify:</B> Org Admin no longer sees the user listed on the invitations page. </li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminInviteMemberResendAndRevoke() {

		Member member = new Member(CommunityRole.MEMBERS, testUser2);		    		    	

		log.info("INFO: Get the UUID of community");
		community5.getCommunityUUID_API(apiOwner, comAPI5);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);		
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community5.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Make sure each of these buttons appear on the full Members page: Add Members, Invite Members, Import Members, Export Members");
		verifyButtonsOnMembersPage();

		log.info("INFO: Adding member via Invited member button");
		try {
			ui.inviteMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Click on the Send Invitations button");
		ui.clickLinkWait(CommunitiesUIConstants.SendInvitesButton);

		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);

		log.info("INFO: Click the Resend link");
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberResendLink);

		log.info("INFO: Verify the resend success message displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getInviteSuccessMsg(member.getUser())),
				"ERROR: The resend success message did not display");

		log.info("INFO: Click on the Revoke link");
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberRevokeLink);

		log.info("INFO: Select ok");
		ui.clickLinkWait(CommunitiesUIConstants.okButton);

		log.info("INFO: Verify the invited user no longer appears on the Invitations tab");
		Assert.assertFalse(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
				"ERROR: The invited user is still present on the page");

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Org Admin (non-member)- Import Member </li>
	 * <li><B>Info:</B> This test will verify an Org Admin who is not a member of a community can import members</li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The Org Admin can see the Add, Invite, Import and Export buttons</li>
	 * <li><B>Step:</B> Org Admin clicks on the Import Members button</li>
	 * <li><B>Step:</B> Enter (2) users to be imported as Members</li>
	 * <li><B>Step:</B>) Click on the Import link</li>
	 * <li><B>Verify:</B> The users are imported with Member access </li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminImportMember() {

		Member member1 = new Member(CommunityRole.MEMBERS, testUser2);		   
		Member member2 = new Member(CommunityRole.MEMBERS, testUser3);
		boolean banner;

		log.info("INFO: Get the UUID of community");
		community6.getCommunityUUID_API(apiOwner, comAPI6);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community6.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Make sure each of these buttons appear on the full Members page: Add Members, Invite Members, Import Members, Export Members");
		verifyButtonsOnMembersPage();

		log.info("INFO: Click the Import Members button");
		ui.clickLinkWait(CommunitiesUIConstants.ImportMembersButton);

		log.info("INFO: Enter the email address of the (2) users to be imported");
		driver.getSingleElement(CommunitiesUIConstants.MembersImportTextArea).type(testUser2.getEmail()+","+testUser3.getEmail());

		log.info("INFO: Click the Import button");
		ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
		
		log.info("INFO: Verify the message 'You have successfully added the following members to this community ... ' displays");
		banner=(ui.fluentWaitTextPresent(Data.getData().ImportMemberMsg) && ui.fluentWaitTextPresent(testUser3.getDisplayName()) 
				&& ui.fluentWaitTextPresent(testUser2.getDisplayName())); 
		Assert.assertTrue(banner,
				"ERROR: The message that the users have been successfully added does not appear");

		log.info("INFO: Verify that " + testUser2.getDisplayName() + " was added to the community");
		Assert.assertTrue(ui.isElementPresent("link=" + testUser2.getDisplayName()),
				"ERROR: The user " + testUser2.getDisplayName() + " does not appear on the Members page");

		log.info("INFO: Collect the member access level for: " + testUser2.getDisplayName());
		String member1Info = ui.getMemberElement(member1).getText();

		log.info("INFO: Verify that " + testUser2.getDisplayName() + " has Member access");
		Assert.assertTrue(member1Info.contains("Member"),
				"ERROR: User " + testUser2.getDisplayName() + " does not contain member access");
		
		log.info("INFO: Verify that " + testUser3.getDisplayName() + " was added to the community");
		Assert.assertTrue(ui.isElementPresent("link=" + testUser3.getDisplayName()),
				"ERROR: The user " + testUser3.getDisplayName() + " does not appear on the Members page");

		log.info("INFO: Collect the member access level for: " + testUser3.getDisplayName());
		String member2Info = ui.getMemberElement(member2).getText();

		log.info("INFO: Verify that " + testUser3.getDisplayName() + " has Member access");
		Assert.assertTrue(member2Info.contains(Data.getData().memberRole),
				"ERROR: User " + testUser2.getDisplayName() + " does not contain member access");
		
		ui.endTest();
	}
	
	/**
	 * <ul> 
	 * <li><B>Test Scenario:</B> Org Admin (non-member)adds a user with 'Owner' access to the community and changes their access to 'Member' </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Step:</B> Add a user with Owner access</li>
	 * <li><B>Step:</B> Edit user's access level - change from Owner to Member</li>
	 * <li><B>Verify:</B> The access level was changed from Owner to Member without error</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminNonMemberPublicCommChangeOwnerToMember() {

		Member owner = new Member(CommunityRole.OWNERS, testUser2);


		log.info("INFO: Get the UUID of community");
		community7.getCommunityUUID_API(apiOwner, comAPI7);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community7.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Add a user with owner access to the community");		
		try {
			ui.addMemberCommunity(owner);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Select save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberSaveButton).click();

		log.info("INFO: Click on the Edit link under the member named: " + owner.getUser().getDisplayName());
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);
		
		log.info("INFO: Select the radio button option 'Member'");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberRadioButton).click();

		log.info("INFO: Click on the Edit Member pop-up Save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditMemberDialogSaveButton).click();

		log.info("INFO: Get the access level for: " + owner.getUser().getDisplayName());
		String memberInfo = ui.getMemberElement(owner).getText();

		log.info("INFO: Verify the user " + owner.getUser().getDisplayName() + " now has Member access");
		Assert.assertTrue(memberInfo.contains(Data.getData().memberRole),
				"ERROR: The user's access level did not get changed to Member access");	
		
		ui.endTest();
	}

	/**
	 * <ul> 
	 * <li><B>Test Scenario:</B> Org Admin (non-member)adds a user with 'Member' access to the community and changes their access to 'Owner' </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the Org Admin</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Step:</B> Add a user with Owner access</li>
	 * <li><B>Step:</B> Edit user's access level - change from Member to Owner</li>
	 * <li><B>Verify:</B> The access level was changed from Member to Owner without error</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminNonMemberPublicCommChangeMemberToOwner() {

		Member member = new Member(CommunityRole.MEMBERS, testUser2);


		log.info("INFO: Get the UUID of community");
		community8.getCommunityUUID_API(apiOwner, comAPI8);

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community8.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Add Member to community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Select save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberSaveButton).click();

		log.info("INFO: Click on the Edit link under the member named: " + member.getUser().getDisplayName());
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);
		
		log.info("INFO: Select the radio button option 'Owner'");
		ui.getFirstVisibleElement(CommunitiesUIConstants.OwnerRadioButton1).click();

		log.info("INFO: Click on the Edit Member pop-up Save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditMemberDialogSaveButton).click();

		log.info("INFO: Get the access level for: " + member.getUser().getDisplayName());
		String memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Verify the user " + member.getUser().getDisplayName() + " now has Owner access");
		Assert.assertTrue(memberInfo.contains(Data.getData().ownerRole),
				"ERROR: The user's access level did not get changed to Owner access");	

		
		ui.endTest();
	}
	
	/**
	 * <ul> 
	 * <li><B>Test Scenario:</B> Org Admin who is a member of the internal community adds a user and then makes them the business owner </li>
	 * <li><B>Step:</B> Create an internal community using the API </li>
	 * <li><B>Step:</B> Log into Communities as the business owner</li>
	 * <li><B>Step:</B> Add the org Admin to the community with 'Member' access</li>
	 * <li><B>Step:</B> Business Owner logs out and the org Admin logs in
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Step:</B> Add an internal user to the community with 'Member' access</li>
	 * <li><B>Step:</B> Edit the user's access level and make the user the new business owner</li>
	 * <li><B>Verify:</B> The user with Member access is now the business owner</li>
	 * <li><B>Verify:</B> The original business owner now has Owner access</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E832B062FD05CDF4852580E50053E757"> TTT: ORG ADMINS SHOULD BE ABLE TO UPDATE COMMUNITY MEMBERSHIP PAGE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminInternalCommChangeMemberToBizOwner(){

		Member orgAdmin = new Member(CommunityRole.MEMBERS, adminUser);
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member bizOwner = new Member(CommunityRole.OWNERS, testUser1);


		log.info("INFO: Get the UUID of community");
		community9.getCommunityUUID_API(apiOwner, comAPI9);

		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community9.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();

		log.info("INFO: Add the org admin to the community - 'Member' access");		
		try {
			ui.addMemberCommunity(orgAdmin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Select save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberSaveButton).click();
		
		log.info("INFO: Log out as the business owner");
		ui.logout();
		
		log.info("INFO: Log in as the org admin: " + adminUser.getDisplayName());
		ui.login(adminUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(ui);
		
		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		checkTabbedNavGKAndNavigateToMembers();
		
		log.info("INFO: Add a member to the community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Select save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.MemberSaveButton).click();
		
		log.info("INFO: Make sure each of these buttons appear on the full Members page: Add Members, Invite Members, Import Members, Export Members");
		verifyButtonsOnMembersPage();

		log.info("INFO: Click on the Edit link under the member named: " + member.getUser().getDisplayName());
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);
		
		log.info("INFO: Verify the Edit Member pop-up displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.MemberEditionPopUp),
		"ERROR: The Edit Member pop-up box does not appear");

		log.info("INFO: Click on the Business Owner radio button");
		selectBusinessOwnerRadioButton();

		log.info("INFO: Click on the Edit Member pop-up Save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditMemberDialogSaveButton).click();

		log.info("INFO: Click on the OK button on the change business owner confirmation alert message");
		Alert changeBizOwnerAlert=driver.switchToAlert();
		changeBizOwnerAlert.accept();	

		log.info("INFO: Get the access level for: " + member.getUser().getDisplayName());
		String memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Verify the community member " + member.getUser().getDisplayName() + " is now the Business Owner");
		Assert.assertTrue(memberInfo.contains(Data.getData().businessOwnerRole),
				"ERROR: The original Member is not listed as the Business Owner");	

		log.info("INFO: Get the access level for: " + bizOwner.getUser().getDisplayName());
		String ownerInfo = ui.getMemberElement(bizOwner).getText();

		log.info("INFO: Verify the original Business Owner " + bizOwner.getUser().getDisplayName() + "is now an Owner");
		Assert.assertTrue(ownerInfo.contains(Data.getData().ownerRole),
				"ERROR: The original Business Owner is not listed as the Owner");

		ui.endTest();
	}

	
	
	/**
	 * Check to see if the Tabbed Nav GK setting is enabled. 
	 * If GK setting is enabled, click on the Members tab from the tabbed nav menu.
	 * If GK setting is not enabled, click on Members link from the left nav menu.
	 */	
	private void checkTabbedNavGKAndNavigateToMembers() {	
		String gk_flag;
		boolean value;
		gk_flag = Data.getData().commTabbedNav;
		value = ui.checkGKSetting(Data.getData().commTabbedNav);

		if(value){

			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");

			log.info("INFO: Click on the Members tab");    		
			Community_TabbedNav_Menu.MEMBERS .select(ui);

		}else { 

			log.info("INFO: Clicking on the communities link");
			Community_LeftNav_Menu.MEMBERS .select(ui);
		}	
	}

	/**
	 * Check to make sure each of the following buttons appear on the full Members page: Add Members, Invite Members, Import Members and Export Members
	 */		    
	private void verifyButtonsOnMembersPage(){
		log.info("INFO: Verify the Add Members button appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddMembersToExistingCommunity),
				"ERROR: The Add Members button does not appear on the full Members page");

		log.info("INFO: Verify the Invite Members button appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.InviteMemberButton),
				"ERROR: The Invite Members button does not appear on the full Members page");

		log.info("INFO: Verify the Import Members button appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ImportMembersButton),
				"ERROR: The Import Members button does not appear on the full Members page");

		log.info("INFO: Verify the Export Members button appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ExportMembersButton),
				"ERROR: The Export Members button does not appear on the full Members page");

	}
	
   /**
    * Get a list of Business Owner radio buttons and click the first visible radio button.
    */
	public void selectBusinessOwnerRadioButton() {
		List <Element> radioButtons= driver.getVisibleElements(CommunitiesUIConstants.businessOwnerRadioButton);
		radioButtons.get(0).click();
	}
}
