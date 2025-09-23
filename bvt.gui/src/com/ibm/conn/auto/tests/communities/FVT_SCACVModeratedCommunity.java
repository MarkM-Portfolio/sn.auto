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

public class FVT_SCACVModeratedCommunity extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCACVModeratedCommunity.class);
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
		
		//Load User
		businessOwner = cfg.getUserAllocator().getUser();		
		ownerUser = cfg.getUserAllocator().getUser();
		memberUser = cfg.getUserAllocator().getUser();	
		invitedUser = cfg.getUserAllocator().getUser();			
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
		// check owner view
		Assert.assertTrue(driver.isElementPresent( "link=" +  sCommunityName), "See it at owner view");
	} 
	
	private void CheckFollowingView(String sCommunityName) {

		log.info("  INFO: Check for following view");
		
		// Check for following view
		ui.clickLinkWait(CommunitiesUICloud.FollowView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" +sCommunityName), "See it at following view");
	} 
	
	private void CheckInvitedView(String sCommunityName) {

		log.info("  INFO: Check for Invited view");
		
		// Check for following view
		ui.clickLinkWait(CommunitiesUICloud.InvitedView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" +sCommunityName), "See it at invited view");
	} 
	
	public void VerifyInviteUser(String Message) throws Exception {

		log.info("INFO: Verify invite a user " );
	
		ui.fluentWaitTextPresent(Message); 
		Assert.assertTrue(driver.isTextPresent(Message), " User be invited");
	} 
	
	private void CheckCompanyView(String sCommunityName) {

		log.info("  INFO: Check for company view");
		
		// Check for following view
		ui.clickLinkWait(CommunitiesUICloud.CompanyView);	
		ui.waitForSameTime();
		Assert.assertTrue(driver.isElementPresent( "link=" +sCommunityName), "See it at company view");
	} 
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Info: Check the community catalog view for an Moderated community </B></li>
	 *<li><B>Steps: 
	 * Login as business user with testing company setting for an internal community.
	 * Create an internal restricted community
	 * Check the catalog view as an owner
	 * Check the catalog view as a member
	 * Check the catalog view as a following
	 * delete the community.
	 * Logout.
	 * 
	</B> </li>
	 *<li><B>Verify: </B> </li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud"})
	public void ModeratedBusinessOwner() throws Exception {

		log.info("INFO: Check Community Action Catalog view as a Business Owner");
		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();
	
				
		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.MODERATED)
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		ui.waitForSameTime();
		
		community.create(ui);
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
			
		// check company view
		CheckCompanyView(community.getName());
		
		ui.openCommunity(community.getName());
		
		// delete it
		ui.delete(community, businessOwner); 
			
		ui.logout(); 
		ui.endTest(); 
	} 
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Info: Check the community catalog view for an ixternal Restriced community </B></li>
	 *<li><B>Steps: 
	 * Login as an owner user with testing company setting for an internal community.
	 * Create an internal restricted community
	 * Add an owner member
	 * Log in as the owner user
	 * Check the catalog view as an owner
	 * Check the catalog view as a member
	 * Check the catalog view as a following
	 * delete the community.
	 * Logout.
	 * 
	 */
	@Test(groups = {"fvtcloud"})
	public void  ModeratedOnwer() throws Exception {

		log.info("INFO: Check Community Action Catalog view as Owner");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.MODERATED)
											.addMember(new Member(CommunityRole.OWNERS, ownerUser))
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
		ui.login(ownerUser);
		
		ui.waitForSameTime();
		// check owner view
		CheckOwnerView(community.getName());
		
		// check member view
		CheckMemberView(community.getName());
		
		// check following view
		CheckFollowingView(community.getName());
			
		// check company view
		CheckCompanyView(community.getName());
				
		// delete it
		ui.openCommunity(community.getName());
		ui.delete(community, ownerUser); 
		
		ui.logout(); 
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Info: Check the community catalog view for an internal Restriced community </B></li>
	 *<li><B>Steps: 
	 * Login as an owner user with testing company setting for an internal community.
	 * Create an internal restricted community
	 * Add a member user
	 * Log in as the member user
	 * Check the catalog view as a member
	 * Logout.
	 * 
	</B> </li>
	 *<li><B>Verify: </B> </li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud"})
	public void ModeratedMember() throws Exception {

		log.info("INFO: Check Community Action Catalog view as a member");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.MODERATED)
											.addMember(new Member(CommunityRole.MEMBERS, memberUser))
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
		
		// check company view
		CheckCompanyView(community.getName());
		
		ui.logout(); 
		driver.quit();
		
		// delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);	
	
		ui.openCommunity(community.getName());
		ui.delete(community, businessOwner); 
		
		ui.logout(); 
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Info: Check the community catalog view for an internal Restriced community </B></li>
	 *<li><B>Steps: 
	 * Login as an owner user with testing company setting for an internal community.
	 * Create an internal restricted community
	 * Invite internal user
	 * Logout
	 * Log in as the invited user
	 * Check the catalog view as I'm invited
	 * Logout.
	 * 
	</B> </li>
	 *<li><B>Verify: </B> </li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud"})
	public void ModeratedInvited() throws Exception {
		log.info("INFO: Check Community Action Catalog view as an invited user");
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
			
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.MODERATED)
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
		
		community.create(ui);
		
		ui.waitForSameTime();
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
		
		// check company view
		CheckCompanyView(community.getName());
		
		ui.logout(); 
		driver.quit();
		
		// delete it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);	
	
		ui.openCommunity(community.getName());
		ui.delete(community, businessOwner); 
		
		ui.logout(); 
		ui.endTest(); 
	} 
}
