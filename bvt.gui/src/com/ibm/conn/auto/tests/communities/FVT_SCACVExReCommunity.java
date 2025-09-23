package com.ibm.conn.auto.tests.communities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;

public class FVT_SCACVExReCommunity extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCACVExReCommunity.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User ownerUser, UserB, UserC, UserD, UserE, UserF;
	
	String CommunityName;
	private BaseCommunity community;

	String InviteUserMsg = "You have successfully invited the following people to this community";	

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Load User
		ownerUser = cfg.getUserAllocator().getUser();	
		UserB = cfg.getUserAllocator().getUser();		
		UserC = cfg.getUserAllocator().getUser();		

		UserD = cfg.getUserAllocator().getGroupUser("ext1");		// external member
		
		UserE = cfg.getUserAllocator().getUser();					// internal invited user
		UserF = cfg.getUserAllocator().getGroupUser("ext2");		// external invited member
		
		log.info("INFO: Using test user: " + ownerUser.getDisplayName());
	}

	private void CheckOwnerView(String sTitle) throws Exception {

		log.info("  INFO: Check for onwer view");	
	
		// check owner view
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" + sTitle), "See it at owner view");
	} 
	
	private void CheckMemberView(String sTitle) throws Exception {

		log.info("  INFO: Check for member view");
		
		// Check for member view
		ui.clickLinkWait(CommunitiesUICloud.memberView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" + sTitle), "See it at member view");
	} 
	
	private void CheckFollowingView(String sTitle) throws Exception {

		log.info("  INFO: Check for following view");
		
		// Check for following view
		ui.clickLinkWait(CommunitiesUICloud.FollowView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" + sTitle), "See it at member view");
	} 
	
	private void CheckInvitedView(String sCommunityName) {

		log.info("  INFO: Check for Invited view");
		
		// Check for following view
		ui.clickLinkWait(CommunitiesUICloud.InvitedView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" +sCommunityName), "See it at member view");
	} 
	
	public void VerifyInviteUser(String Message) throws Exception {

		log.info("INFO: Verify invite a user " );
	
		ui.fluentWaitTextPresent(Message); 
		Assert.assertTrue(driver.isTextPresent(Message), " User be invited");
	} 
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Add a owner member</li>
	 *<li><B>Verify:</B>Check the Owner, member and following views for the owner member</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *<li><B>Verify:</B>Check the community has been deleted</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ExBusinessOwner() throws Exception {

		log.info("INFO: Check external Community Action Catalog view as Business Owner");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.shareOutside(true) 		
											.build();  
		
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		community.create(ui);
		ui.waitForSameTime();
		ui.logout();
		driver.quit();
		
		log.info("INFO: Check Community Action Catalog view as Busenes Owner");
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.waitForSameTime();
		
		// check onwer view
		CheckOwnerView(CommunityName);
		
		// Check for member view
		CheckMemberView(CommunityName);
	
		// check following view
		CheckFollowingView(CommunityName);
		
		ui.openCommunity(CommunityName);
		// delete it
		ui.delete(community, ownerUser);
	
		ui.logout();		
		ui.endTest();
	}  
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Add a owner member</li>
	 *<li><B>Verify:</B>Check the Owner, member and following views for the owner member</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *<li><B>Verify:</B>Check the community has been deleted</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ExCommunityOwner() throws Exception {

		log.info("INFO: Check external Community Action Catalog view as Owner user");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.addMember(new Member(CommunityRole.OWNERS, UserB))
											.shareOutside(true) 		
											.build();  
		
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		community.create(ui);
		ui.waitForSameTime();
		
		ui.logout();
		driver.quit();
		
		log.info("INFO: Check Community Action Catalog view as Owner member");
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(UserB);

		ui.waitForSameTime();
		
		// check onwer view
		CheckOwnerView(CommunityName);
		
		// Check for member view
		CheckMemberView(CommunityName);
	
		// check following view
		CheckFollowingView(CommunityName);
		
		ui.openCommunity(CommunityName);
		// delete it - user B is an owner user
		ui.delete(community, UserB);
	
		ui.logout();		
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Add a member user</li>
	 *<li><B>Step:</B>Log out</li>
	 *<li><B>Step:</B>Log in as the member user</li>
	 *<li><B>Verify:</B>Check the member view</li>
	 *<li><B>Step:</B>Logout</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ExCommInternalMember() throws Exception {

		log.info("INFO: Check external Community Action Catalog view as an internal member");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.addMember(new Member(CommunityRole.MEMBERS, UserC))
											.shareOutside(true) 		
											.build();  
		
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		community.create(ui);
		ui.waitForSameTime();
		ui.logout();
		driver.quit();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(UserC);

		ui.waitForSameTime();
		
		// Check for member view
		CheckMemberView(CommunityName);
		ui.logout();		
		driver.quit();
		
		// owner delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.waitForSameTime();
		ui.openCommunity(CommunityName);
		ui.delete(community, ownerUser);	
		
		ui.logout();		
		ui.endTest();
	} 	

	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Add an external member</li>
	 *<li><B>Step:</B>Log out</li>
	 *<li><B>Step:</B>Log in as an external member?</li> 
	 *<li><B>Verify:</B>Check the member view</li>
	 *<li><B>Step:</B>Logout</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ExCommExternalMember() throws Exception {

		log.info("INFO: Check external Community Action Catalog view as an external member");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.addexMember(new Member(CommunityRole.OWNERS, UserD))
											.shareOutside(true) 		
											.build();  
		
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		community.create(ui);
		ui.waitForSameTime();
		ui.logout();
		driver.quit();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(UserD);

		ui.waitForSameTime();
		
		// Check for member view for an external user
		CheckMemberView(CommunityName);
	
		ui.logout();			
		driver.quit();
		
		// owner delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.waitForSameTime();
		ui.openCommunity(CommunityName);
		ui.delete(community, ownerUser);	
		
		ui.logout();		
		ui.endTest(); 
	} 	

	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Invited an internal member user</li>
	 *<li><B>Step:</B>Log out</li>
	 *<li><B>Step:</B>Log in as a invited member user</li>
	 *<li><B>Verify:</B>Check the invited view</li>
	 *<li><B>Step:</B>Logout</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ExInvitedInternalMember() throws Exception {

		log.info("INFO: Check external Community Action Catalog view as an internal invited user");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.shareOutside(true) 		
											.build();  
		
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		community.create(ui);
		
		ui.inviteUser(UserE);
		VerifyInviteUser(InviteUserMsg);
		
		ui.logout();
		driver.quit();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(UserE);

		ui.waitForSameTime();
		
		// Check for member view
		CheckInvitedView(CommunityName);
	
		ui.logout();			
		driver.quit();
		
		// owner delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.waitForSameTime();
		ui.openCommunity(CommunityName);
		ui.delete(community, ownerUser);	
		
		ui.logout();		
		ui.endTest();
	} 	

	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Invited an external member user</li>
	 *<li><B>Step:</B>Log out</li>
	 *<li><B>Step:</B>Log in as a external invited member user</li>
	 *<li><B>Verify:</B>Check the invited view</li>
	 *<li><B>Step:</B>Logout</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ExInvitedExternalMember() throws Exception {

		log.info("INFO: Check external Community Action Catalog view as an internal invited user");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.shareOutside(true) 		
											.build();  
		
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		community.create(ui);
		
		ui.inviteExternalUser(UserF);
		VerifyInviteUser(InviteUserMsg);
		
		ui.logout();
		driver.quit();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(UserF);

		ui.waitForSameTime();
		
		// Check for member view
		CheckInvitedView(CommunityName);
		ui.logout();	
		driver.quit();
		
		// owner delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.waitForSameTime();
		ui.openCommunity(CommunityName);
		ui.delete(community, ownerUser);	
		
		ui.logout();		
		ui.endTest();  
	} 	

}
