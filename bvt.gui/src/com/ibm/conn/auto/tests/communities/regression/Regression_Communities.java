package com.ibm.conn.auto.tests.communities.regression;

import java.util.EnumSet;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Regression_Communities extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Regression_Communities.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser, testUser1, testLookAheadUser;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
}
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();		

		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	/**
	 * Creates a public community, and attempts to create three subcommunities,
	 * one public, one moderated and one private. Then, the test adds a
	 * subcommunity widget to the parent community and verifies that the
	 * subcommunites appear in the widget.
	 * @throws Exception
	 */	
	@Test(groups = {"regression"}  , enabled=false )
	public void createPublicCommunityWithSubcommunities() throws Exception {

		String testName = ui.startTest();
		
		BaseCommunity mainCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = mainCommunity.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		mainCommunity.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the subcommunities widget to the community
		log.info("INFO: Adding the " + BaseWidget.SUBCOMMUNITIES.getTitle() +
				" widget to community " + mainCommunity.getName() + " using API");
		mainCommunity.addWidgetAPI(comAPI, apiOwner, BaseWidget.SUBCOMMUNITIES);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);

		//Login as a random user
		ui.login(testUser);

		//navigate to the main community
		log.info("INFO: Navigate to the community using UUID");
		mainCommunity.navViaUUID(ui);
		
		//Create public subcommunity
		BaseSubCommunity publicSub = new BaseSubCommunity.Builder("Public Sub" + Helper.genDateBasedRand())
								.tags(Data.getData().commonTag + Helper.genDateBasedRand())
								.access(BaseSubCommunity.Access.PUBLIC)
								.description("Test description for testcase " + testName).build();
		ui.createSubCommunity(publicSub);		
		
		//Back to parent community
		ui.clickLinkWait(CommunitiesUIConstants.mainCommunityLinkFromSubcommunity);
		
		//Create public subcommunity
		BaseSubCommunity moddedSub = new BaseSubCommunity.Builder("Moderated Sub" + Helper.genDateBasedRand())
								.tags(Data.getData().commonTag + Helper.genDateBasedRand())
								.access(BaseSubCommunity.Access.MODERATED)
								.description("Test description for testcase " + testName).build();
		ui.createSubCommunity(moddedSub);		
		
		//Back to parent community
		ui.clickLinkWait(CommunitiesUIConstants.mainCommunityLinkFromSubcommunity);
		
		//Create public subcommunity
		BaseSubCommunity restrictedSub = new BaseSubCommunity.Builder("Restricted Sub" + Helper.genDateBasedRand())
								.tags(Data.getData().commonTag + Helper.genDateBasedRand())
								.access(BaseSubCommunity.Access.RESTRICTED)
								.description("Test description for testcase " + testName).build();
		ui.createSubCommunity(restrictedSub);		
		
		//Back to parent community
		ui.clickLinkWait(CommunitiesUIConstants.mainCommunityLinkFromSubcommunity);
		
		//Verify that the subcommunities are displaying in the widget
		log.info("INFO: verifying " + publicSub.getName() + " exists in subcommunity widget");
		Assert.assertTrue(ui.isElementPresent("css=div#subcommunityNavWidgetContainer"
										      + " a[title='" + publicSub.getName()
										      + "'] img"),
						  "ERROR: " + publicSub.getName() + " is not listed in the subcommunity widget");
		log.info("INFO: verifying " + moddedSub.getName() + " exists in subcommunity widget");
		Assert.assertTrue(ui.isElementPresent("css=div#subcommunityNavWidgetContainer"
										      + " a[title='" + moddedSub.getName()
										      + "'] img"),
					      "ERROR: " + moddedSub.getName() + " is not listed in the subcommunity widget");
		log.info("INFO: verifying " + restrictedSub.getName() + " exists in subcommunity widget");
		Assert.assertTrue(ui.isElementPresent("css=div#subcommunityNavWidgetContainer"
										      + " a[title='" + restrictedSub.getName()
										      + "'] img"),
					      "ERROR: " + restrictedSub.getName() + " is not listed in the subcommunity widget");
		
		ui.endTest();
	}
	/**
	 * Confirm all themes are available
	 * @throws Exception
	 */
	@Test(groups = {"regression"}  , enabled=false )
	public void customThemesExist() throws Exception {
		ui.startTest();
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
				
		//Login as a user (ie. Amy Jones66)
		ui.login(testUser);
		
		//Click Start A Community
		log.info("INFO: Create a new community using Start A Community button");
		ui.clickLink(CommunitiesUIConstants.StartACommunity);

		//Wait for Community page to load
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		
		//Expand themes
		ui.clickLink(CommunitiesUIConstants.CommunityThemeLink);
		
		EnumSet<BaseCommunity.Theme> themes = EnumSet.allOf(BaseCommunity.Theme.class);
		
		for (BaseCommunity.Theme theme : themes) {
			log.info("INFO: Verifying theme: " + theme);
			Assert.assertTrue(ui.isElementPresent(theme.themeLink),
					"Theme " + theme + " could not be found on the page");
		}

		ui.endTest();
	}
	
	/**
	 * Test Scenario: Verify inviting a user to a public community and then re-sending the invite and finally revoking the invite
	 *<ul>
	 *<li><B>Info:</B>Verify inviting, re-sending, and revoking an invitation</li>
	 *<li><B>Step:</B>Create a public community using the API</li>
	 *<li><B>Step:</B>Invite a user to the community</li>
	 *<li><B>Verify:</B>The invited user appears on the Invitations tag</li>
	 *<li><B>Step:</B>Select the user & re-send the invitation</li>
	 *<li><B>Verify:</B>The invitation was resent</li>
	 *<li><B>Step:</B>Revoke the invitation</li>
	 *<li><B>Verify:</B>The revoke confirmation dialog displays</li>
	 *<li><B>Step:</B>Click OK on the confirmation dialog</li>
	 *<li><B>Verify:</B>The user no longer appears on the Invitations tab
	 *</ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression"} )
	public void inviteAndRevokeCommunityMember() throws Exception {
		
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		                              .tags(Data.getData().commonTag + Helper.genDateBasedRand())
		                              .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		                              .access(Access.PUBLIC)
		                              .description("Test description for testcase " + testName)
		                              .build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
				
		//Login as the community creator 
		log.info("INFO: Login with user " + testUser.getDisplayName());
		ui.login(testUser);

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Choose members from the left nav
		log.info("INFO: Click on Members link from left nav. bar");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Invite a user to the community
		log.info("INFO: Invite a user to the community");
		ui.inviteMemberCommunity(member);

		log.info("INFO: Click on the Send Invitations link");
		ui.clickLinkWait(CommunitiesUIConstants.SendInvitesButton);
		
		//View Invitations tab under Members section of the community
		log.info("INFO: Click on the Invitations tab");
		ui.clickLink(CommunitiesUIConstants.InvitationsTab);
		
		//find the link to our user in the invites
		log.info("INFO: Verify the invited user, " +testUser1.getDisplayName() +" ,appears on the Invitations tab ");
		Assert.assertTrue(driver.isElementPresent("link=" + member.getUser().getDisplayName()),
				"ERROR: The invited member is NOT present on the page");
							
		//Resend the invitation - not sure this works for Smartcloud, assumes link is JavaScript
		log.info("INFO: Select the user to resend the invite to");
		ui.selectInvitedMember(member);
		
		log.info("INFO: Click on the Resend link");
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberResendLink);
		
		//Confirm invitation resend text appears
		log.info("INFO: Verify the Resend was successful message displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().inviteUserSuccess),
				          "ERROR: The successfully invited message did NOT display");
			
		//Revoke invitation
		
		//Select the user to revoke
		log.info("INFO: Select the user to resend the invite to");
		ui.selectInvitedMember(member);
		
		//Click the revoke link and confirm prompt appears
		log.info("INFO: Click on the Revoke link");		
		ui.clickLinkWait(CommunitiesUIConstants.InvitedMemberRevokeLink);
		
		log.info("INFO: Verify the revoke confirmation dialog displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().inviteRevokePrompt),
				          "ERROR: The revoke confirmation dialog did NOT display");
		
		//Click OK button to confirm revoke action
		log.info("INFO: Click OK to confirm revoking the user");
		ui.clickLink(CommunitiesUIConstants.RevokeInviteOK);
		
		//Confirm invitation of user does not appear
		log.info("INFO: Verify the revoked user no longer appears on the Invitation tab" );
		Assert.assertFalse(ui.isElementPresent("link=" + testLookAheadUser.getDisplayName()),
				          "ERROR: The revoked user is still listed on the Invitation tab");
		
		ui.endTest();
	}

	/**
	 * Create public community and confirm invite appears
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false  )
	public void inviteAndConfirmInviteAppears() throws Exception {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.PUBLIC)
		.description("Test description for testcase " + testName)
		.theme(BaseCommunity.Theme.GREEN).build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
				
		//Login as a user (ie. Amy Jones66)
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Choose members from the left nav
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		//Then click on the Invite Members button
		ui.clickLinkWait(CommunitiesUIConstants.InviteMemberButton);
		
		//Fill in the form to invite a user
		ui.inviteUser(testLookAheadUser);
		
		//Check the invited user appears on the page
		Assert.assertTrue(ui.fluentWaitTextPresent("You have successfully invited the following people to this community: "
													+ testLookAheadUser.getDisplayName()));
		
		//Logout
		ui.logout();
		ui.fluentWaitPresent(BaseUIConstants.Login_Link);
		
		//Go to the list of communities the user is invited to
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);
		ui.clickLink(CommunitiesUIConstants.IamInvited);

		//Login as invited user
		ui.login(testLookAheadUser);
		
		//Verify the community appears in the list
		Assert.assertTrue(ui.fluentWaitPresent("link=" + community.getName()));
		
		ui.endTest();
	}

	/**
	 * Create public community and confirm invite appears
	 * ...is what the original comment says, but the test code does not invite
	 * anybody. The test actually creates a public community with a custom
	 * theme and tests that an uninvited user can follow it.
	 * @throws Exception
	 */
	@Test(groups = {"regression"}  , enabled=false )
	public void userCanFollowPublicCommunity() throws Exception {
		
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.PUBLIC)
		.description("Test description for testcase " + testName)
		.theme(BaseCommunity.Theme.GREEN).build();
		
		//create community
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as random user
		ui.login(testLookAheadUser);
		log.info("INFO: Select Public Communities view");
		Community_View_Menu.PUBLIC_COMMUNITIES.select(ui);
		
		//Follow The Community
		ui.follow(community);
		
		//Verify Link Changes to Stop Following This Community
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		//Go to Im Following View
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);
		Community_View_Menu.IM_FOLLOWING.select(ui);
		
		//verify the community shows up in the list
		Assert.assertTrue(ui.isElementPresent("link=" + community.getName()));
				
		//Open Link and verify action bar says "Stop Following this Community"
		ui.clickLink("link=" + community.getName());
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		ui.endTest();
	}
	
	/**
	 * Create modified community and verify members form.
	 * 
	 * What is the purpose of this test? There is no way for this test to fail
	 * if the PeopleToJoin test passes! / Hakan
	 * @throws Exception
	 */
	@Test(groups = {"regression"} )
	public void communitiesTestVerifyMembersForm() throws Exception
	{
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.MODERATED)
		.description("Test description for testcase " + testName)
		.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);		

		//Login as a user (ie. Amy Jones66)
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Go to members
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Click invite members
		ui.clickLink(CommunitiesUIConstants.InviteMemberButton);
		
		//Check that the page has a name field, invite members button, and cancel button
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.InviteMembersToExistingTypeAhead));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.SendInvitesButton));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.inviteCancelButton));
		
		//Logout
		ui.endTest();
	}
	
	/**
	 * Create a moderated community, then invite two members to the community from the
	 * members page. Confirm the members are listed in the invitations pane of the
	 * members page.
	 * @throws Exception
	 */
	@Test(groups = {"regression"})
	public void peopleToJoin() throws Exception
	{
		String testName = ui.startTest();
		User testUser2 = cfg.getUserAllocator().getUser();
		User testUser3 = cfg.getUserAllocator().getUser();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.MODERATED)
		.description("Test description for testcase " + testName)
		.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as a user (ie. Amy Jones66)
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose members from the left nav
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		//Then click on the Invite Members button
		ui.clickLinkWait(CommunitiesUIConstants.InviteMemberButton);
		
		//Fill in the form to invite a user
		ui.inviteUser(testUser2);
		
		//Check the invited user appears on the page
		Assert.assertTrue(ui.fluentWaitTextPresent("You have successfully invited the following people to this community: "
													+ testUser2.getDisplayName()));

		//Then click on the Invite Members button
		ui.clickLink(CommunitiesUIConstants.InviteMemberButton);
				
		//Fill in the form to invite a user
		ui.inviteUser(testUser3);
		
		//Check the invited user appears on the page
		Assert.assertTrue(ui.fluentWaitTextPresent("You have successfully invited the following people to this community: "
													+ testUser3.getDisplayName()));
				
		//go to invitations tab
		ui.clickLink(CommunitiesUIConstants.InvitationsTab);
		
		//Verify member invitations appear
		Assert.assertTrue(driver.isElementPresent("css=div[id='InvitesPanel'] a:contains(" + testUser2.getDisplayName() + ")"));
		Assert.assertTrue(driver.isElementPresent("css=div[id='InvitesPanel'] a:contains(" + testUser3.getDisplayName() + ")"));

		//Logout
		ui.endTest();
	}	
	
	/**
	 * Create a moderated community and then log out. Log in as a user
	 * who is not a member and attempt to follow the community. Verify
	 * the community appears in the user's "I'm following" list and does not
	 * appear in the user's "I'm a member" list.
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void followModeratedCommunity() throws Exception
	{
		String testName = ui.startTest();
		User testUser2 = cfg.getUserAllocator().getUser();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.MODERATED)
		.description("Test description for testcase " + testName)
		.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();
		
		//create community
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as a different user
		ui.login(testUser2);
		
		//Select the new community
		ui.clickLink(CommunitiesUIConstants.publicCommunities);
		
		//wait for the page to load, navigate with the public communities list
		//rather than using the UUID to test the public communities list works
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link=" + community.getName()));
		ui.clickLinkWait("link=" + community.getName());
		
		//Verify Action bar links are correct
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.FollowThisCommunity));
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.JoinACommunity));
		
		//Click Follow This community
		ui.clickLink(CommunitiesUIConstants.FollowThisCommunity);
		
		//Verify button changes to Stop Following this community
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		//Go to My Communities Im Following view
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);
		ui.clickLink(CommunitiesUIConstants.IamFollowing);
		
		//verify the community shows up in the list
		Assert.assertTrue(driver.isElementPresent("link=" + community.getName()));
		
		//Open Link and verify action bar says "Stop Following this Community"
		ui.clickLink("link=" + community.getName());
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		//Open Members Page
		Community_LeftNav_Menu.MEMBERS.select(ui);
		Assert.assertFalse(driver.isElementPresent("css=tr td span a:contains('"
		                                           + testUser2.getDisplayName() + "')"));
		
		ui.endTest();
	}
	
	/**
	 * Create a restricted community, add a user as a member and then log out.
	 * Log in as the member and attempt to follow the community. Verify
	 * the community appears in the user's "I'm following" list.
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void followRestrictedCommunity() throws Exception
	{
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.RESTRICTED)
		.description("Test description for testcase " + testName)
		.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser))
		.shareOutside(false)
		.build();
		
		//create community
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as a different user
		ui.login(testLookAheadUser);
		// Open the  I'm a Member view
		log.info("INFO: Select I'm a Member view");
		Community_View_Menu.IM_A_MEMBER.select(ui);
		
		//Follow The Community
		ui.follow(community);
		
		//Verify Link Changes to Stop Following This Community
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		//Go to Im Following View
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);
		Community_View_Menu.IM_FOLLOWING.select(ui);
		
		//verify the community shows up in the list
		Assert.assertTrue(ui.isElementPresent("link=" + community.getName()));
				
		//Open Link and verify action bar says "Stop Following this Community"
		ui.clickLink("link=" + community.getName());
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		ui.endTest();
	}	

	/**
	 * Create a moderated community and then log out. Log in as a user
	 * who is a member and attempt to follow the community. Verify
	 * the community appears in the Communities mega menu. Attempt to
	 * unfollow the community, then verify the community does not appear
	 * in the user's "I'm following" page or the Communities mega menu.
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void stopFollowingModeratedCommunity() throws Exception
	{
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.MODERATED)
		.description("Test description for testcase " + testName)
		.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();
		
		//create community
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
				
		//Login as a user
		log.info("INFO: Login with user " + testLookAheadUser.getDisplayName());
		ui.login(testLookAheadUser);
		
		//Check if user2 is already following any communities
		log.info("INFO: Check to see if User2 is following any communities");
		ui.clickLink(CommunitiesUIConstants.IamFollowing);
		String otherCommunity = null;
		if(!ui.isTextPresent(Data.getData().noCommunitiesFollowed))
		{
			otherCommunity = driver.getSingleElement("css=td[class='lotusFirstCell'] h4 span a").getText();
		}
		
		// Open the  I'm a Member view
		log.info("INFO: Select I'm a Member view");
		Community_View_Menu.IM_A_MEMBER.select(ui);
		
		//follow the created community
		log.info("INFO: Follow the created community");
		ui.follow(community);
		
		//Verify button changes to stop following a community
		log.info("INFO: Verify that the follow link changes to stop following a community");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		//Open Communities bar on the navigation banner
		log.info("INFO: Click on Communities located on the mega menu");
		driver.getFirstElement(ui.getCommunitiesBanner()).hover();
		
		//Verify the new community shows up under recently updated communities
		log.info("INFO: Verify the new community displays under Recently Updated on the mega-menu drop-down");
		Assert.assertTrue(ui.fluentWaitPresent("css=td[class='lotusLastCell'] a:contains('" +
													      community.getName() + "')"));
		
		//Open Im Following tab
		log.info("INFO: Click on the Communities link");
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);
		
		log.info("INFO: Click on the I'm Following link");
		ui.clickLink(CommunitiesUIConstants.IamFollowing);
		
		//Verify community is listed
		log.info("INFO: Verify that the community is listed in the I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + community.getName()));
		
		//Open the community
		log.info("INFO: Click on the community link");
		ui.clickLink("link=" + community.getName());
		
		//Click Stop Following the Community
		log.info("INFO: Click on the Stop Following the Community link");
		ui.clickLinkWait(CommunitiesUIConstants.StopFollowingThisCommunity);
		driver.navigate().refresh();
		
		//Open Communities bar and verify community is no longer present
		log.info("INFO: Verify that the community is no longer listed ");
		driver.getFirstElement(ui.getCommunitiesBanner()).hover();
		Assert.assertFalse(ui.isElementPresent("css=td[class='lotusLastCell'] a:contains('" +
										           community.getName() + "')"));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.bannerNoCommunitiesFollowed) ||
				ui.isElementPresent("css=td[class='lotusLastCell'] a:contains('" + otherCommunity + "')"));
		
		//Open Im Following
		log.info("INFO: Click on the Communities link");
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);
		
		log.info("INFO: Click on the I'm Following link");
		ui.clickLink(CommunitiesUIConstants.IamFollowing);
		
		//Wait for page to load
		log.info("INFO: Wait for the list of communities to load");
		ui.fluentWaitPresent(CommunitiesUIConstants.StartACommunity);
		
		//Verify community no longer appears
		log.info("INFO: Verify that the community is NOT listed in the view");
		Assert.assertFalse(driver.isElementPresent("link=" + community.getName()));
		Assert.assertTrue(driver.isTextPresent(Data.getData().noCommunitiesFollowed) || 
				driver.isElementPresent("link=" + otherCommunity));
		
		ui.endTest();
	}	
	
	/**
	 * Create a restricted community, add a user as a member and then log out.
	 * Log in as the member and attempt to follow the community. Verify
	 * the community appears in the Communities mega menu. Attempt to
	 * unfollow the community, then verify the community does not appear
	 * in the user's "I'm following" page or the Communities mega menu.
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void stopFollowingRestrictedCommunity() throws Exception
	{
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		.access(Access.RESTRICTED)
		.shareOutside(false)
		.description("Test description for testcase " + testName)
		.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();
		
		//create community
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner);
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);

		//Login as the community's member
		ui.login(testLookAheadUser);
		
		//Check if user2 is already following any communities
		ui.clickLink(CommunitiesUIConstants.IamFollowing);
		String otherCommunity = null;
		if(!ui.isTextPresent(Data.getData().noCommunitiesFollowed))
		{
			otherCommunity = driver.getSingleElement("css=td[class='lotusFirstCell'] h4 span a").getText();
		}
		
		// Open the  I'm a Member view
		log.info("INFO: Select I'm a Member view");
		Community_View_Menu.IM_A_MEMBER.select(ui);
		
		//Follow The created Community
		ui.follow(community);
				
		//Verify Link Changes to Stop Following This Community
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity));
		
		//Open Communities bar on the navigation banner
		driver.getFirstElement(ui.getCommunitiesBanner()).hover();
		
		//Verify the new community shows up under recently updated communities
		Assert.assertTrue(ui.fluentWaitPresent("css=td[class='lotusLastCell'] a:contains('" +
													      community.getName() + "')"));
		
		//Open Im Following tab
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);

		Community_View_Menu.IM_FOLLOWING.select(ui);
		
		
		//Verify community is listed
		Assert.assertTrue(ui.fluentWaitPresent("link=" + community.getName()));
		
		//Open the community
		ui.clickLink("link=" + community.getName());
		
		//Click Stop Following the Community
		ui.clickLinkWait(CommunitiesUIConstants.StopFollowingThisCommunity);
		driver.navigate().refresh();
		
		//Open Communities bar and verify community is no longer present
		driver.getFirstElement(ui.getCommunitiesBanner()).hover();
		Assert.assertFalse(ui.isElementPresent("css=td[class='lotusLastCell'] a:contains('" +
										           community.getName() + "')"));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.bannerNoCommunitiesFollowed) ||
				ui.isElementPresent("css=td[class='lotusLastCell'] a:contains('" + otherCommunity + "')"));
		
		//Open Im Following
		ui.clickLink(CommunitiesUIConstants.CommunitiesLink);
		ui.clickLink(CommunitiesUIConstants.IamFollowing);
		
		//Wait for page to load
		ui.fluentWaitPresent(CommunitiesUIConstants.StartACommunity);
		
		//Verify community no longer appears
		Assert.assertFalse(driver.isElementPresent("link=" + community.getName()));
		Assert.assertTrue(driver.isTextPresent(Data.getData().noCommunitiesFollowed) || 
				driver.isElementPresent("link=" + otherCommunity));
		
		ui.endTest();
	}
}
