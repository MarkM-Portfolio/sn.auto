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

public class FVT_SCACVInReCommunity extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCACVInReCommunity.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User businessOwner, ownerUser, memberUser, invitedUser;
	
	private BaseCommunity community;
	
	String InviteUserMsg = "You have successfully invited the following people to this community";	
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	private void CheckMemberView(String sTitle) throws Exception {

		log.info("  INFO: Check for member view");
		
		// Check for member view
		ui.clickLinkWait(CommunitiesUICloud.memberView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" + sTitle), "See it at member view");
	} 
	
	private void CheckOwnerView(String sCommunityName) {

		log.info("  INFO: Check for onwer view");	
		// need wait few seconds here
		ui.clickLinkWait(CommunitiesUICloud.ownerView);	
		// check owner view
		Assert.assertTrue(driver.isElementPresent( "link=" +  sCommunityName), "See it at owner view");
	} 
	
	private void CheckFollowingView(String sCommunityName) {

		log.info("  INFO: Check for following view");
		
		// Check for following view
		ui.clickLinkWait(CommunitiesUICloud.FollowView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" +sCommunityName), "See it at member view");
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
	 *<li><B>Info:</B>Check the community catalog view for an Internal Restricted community</li>
	 *<li><B>Step:</B>Login as business user with testing company setting for an internal community</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Verify:</B>Check the catalog view as an owner</li>
	 *<li><B>Verify:</B>Check the catalog view as a member</li>
	 *<li><B>Verify:</B>Check the catalog view as a following</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void InternalRestrictedBusinessOwner() throws Exception {

		log.info("INFO: Check Community Action Catalog view as a Business Owner");
		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();
		//Load User
		businessOwner = cfg.getUserAllocator().getUser();		
				
		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.shareOutside(false) 			// internal
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		ui.waitForSameTime();
		
		// check for external community
		ui.checkInternal(community);
		
		community.create(ui);
		// take longer time here, so the catalog will be build within 30 seconds
		ui.waitForSameTime();
		
		ui.logout();
		driver.quit();
		
		// check for business owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		ui.waitForSameTime();
		// check owner view
		CheckOwnerView(community.getName());
		
		// check member view
		CheckMemberView(community.getName());
		
		// check following view
		CheckFollowingView(community.getName());
		
		ui.openCommunity(community.getName());
		
		// delete it
		ui.delete(community, businessOwner); 
			
		ui.logout(); 
		ui.endTest(); 
	} 
	
	/**
	 *<ul>
	 *<li><B>Info: Check the community catalog view for an Internal Restricted community</li>
	 *<li><B>Step:</B>Login as an owner user with testing company setting for an internal community</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Step:</B>Add an owner member</li>
	 *<li><B>Step:</B>Log in as the owner user</li>
	 *<li><B>Verify:</B>Check the catalog view as an owner</li>
	 *<li><B>Verify:</B>Check the catalog view as a member</li>
	 *<li><B>Verify:</B>Check the catalog view as a following</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void InternalRestrictedOnwer() throws Exception {

		log.info("INFO: Check Community Action Catalog view as Owner");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		businessOwner = cfg.getUserAllocator().getUser();	
		ownerUser = cfg.getUserAllocator().getUser();	
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.addMember(new Member(CommunityRole.OWNERS, ownerUser))
											.shareOutside(false) 			// internal
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		community.create(ui);
		// take longer time here, so the catalog will be build within 30 seconds
		ui.waitForSameTime();
		
		ui.logout();
		driver.quit();
		
		// check for owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
		
		ui.waitForSameTime();
		// check owner view
		CheckOwnerView(community.getName());
		
		// check member view
		CheckMemberView(community.getName());
		
		// check following view
		CheckFollowingView(community.getName());
				
		// delete it
		ui.openCommunity(community.getName());
		ui.delete(community, ownerUser); 
		
		ui.logout(); 
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an Internal Restricted community</li>
	 *<li><B>Step:</B>Login as an owner user with testing company setting for an internal community</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Step:</B>Add a member user</li>
	 *<li><B>Step:</B>Log in as the member user</li>
	 *<li><B>Verify:</B>Check the catalog view as a member</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void InternalRestrictedMember() throws Exception {

		log.info("INFO: Check Community Action Catalog view as Owner");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		businessOwner = cfg.getUserAllocator().getUser();	
		memberUser = cfg.getUserAllocator().getUser();	
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.addMember(new Member(CommunityRole.MEMBERS, memberUser))
											.shareOutside(false) 			// internal
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		community.create(ui);
		ui.waitForSameTime();
		
		ui.logout();
		driver.quit();
		
		// check for owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(memberUser);
		
		ui.waitForSameTime();
		
		// check member view
		CheckMemberView(community.getName());
		ui.logout(); 
		driver.quit();
		
		// owner delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);

		ui.waitForSameTime();
		ui.openCommunity(community.getName());
		ui.delete(community, businessOwner);	
		
		ui.logout();		
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info: Check the community catalog view for an Internal Restricted community</li>
	 *<li><B>Step:</B>Login as an owner user with testing company setting for an internal community</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Step:</B>Invite internal user</li>
	 *<li><B>Step:</B>Log in as the invited user</li>
	 *<li><B>Verify:</B>Check the catalog view as I'm invited</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void InRestrictedInvited() throws Exception {
		log.info("INFO: Check Community Action Catalog view as Owner");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		businessOwner = cfg.getUserAllocator().getUser();	
		invitedUser = cfg.getUserAllocator().getUser();	
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.shareOutside(false) 			// internal
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		community.create(ui);
		
		ui.inviteUser(invitedUser);
		VerifyInviteUser(InviteUserMsg);
		
		ui.logout();
		driver.quit();
		
		// check for invited user
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(invitedUser);
		
		ui.waitForSameTime();
		
		// check member view
		CheckInvitedView(community.getName());
		
		ui.logout(); 
		driver.quit();
		
		// owner delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		ui.waitForSameTime();
		ui.openCommunity(community.getName());
		ui.delete(community, businessOwner);	
		
		ui.logout();		
		ui.endTest(); 
	} 
}
