package com.ibm.conn.auto.tests.communities;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
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

public class FVT_SCACVExternalRestricted extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCACVExternalRestricted.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User ownerUser, memberUser;
	
	private BaseCommunity community;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Load User
		ownerUser = cfg.getUserAllocator().getUser();		
		memberUser = cfg.getUserAllocator().getUser();		

		log.info("INFO: Using test user: " + ownerUser.getDisplayName());
	}
	
	public void verifyExternalSetting(String sTitle, String viewName ) throws Exception {

		ui.openCommunity(sTitle);
	
		Assert.assertTrue(driver.isTextPresent(sTitle), "See community at " + viewName);
		log.info("Got this community: " + sTitle + " at " + viewName);
		
		ui.waitForSameTime();
		
		ui.fluentWaitPresent(CommunitiesUICloud.externalIcon);
		Assert.assertTrue(driver.isElementPresent(CommunitiesUICloud.externalIcon), "See external icon");
		log.info("Got the external icon");
		
		Assert.assertTrue(driver.isTextPresent(CommunitiesUICloud.externalMsg), "See external message for a member");
		log.info("Got the external community message");
	} 
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Login as a user with testing company setting for an external community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Verify:</B>Check the catalog view as an owner</li>
	 *<li><B>Verify:</B>Check the catalog view as a following</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ActionCatalogView() throws Exception {

		log.info("INFO: Check External Restricted Community Action Catalog view as Owner");
		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.shareOutside(true) 			// external
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		// check for external community
		checkExternal(community);
		
		community.create(ui);

		// Check the Owner view
		verifyExternalSetting(community.getName(), "Owner view");
		
		ui.logout();
		driver.quit();
		
		//Load component and login
		log.info("INFO: Check Community Action Catalog view as following");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
		
		ui.clickLinkWait(CommunitiesUICloud.FollowView);	
		
		ui.waitForSameTime();
		verifyExternalSetting(community.getName(), "Following View");
		
		ui.fluentWaitPresent(CommunitiesUICloud.FollowBtn);
		Assert.assertTrue(driver.isElementPresent(CommunitiesUICloud.FollowBtn), "Got Following btn");
			
		// delete the top one
		ui.delete(community, ownerUser);
	
		ui.logout();	
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Login as a user with testing company setting for an external community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Add a member</li>
	 *<li><B>Verify:</B>Check the catalog view as a member</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void MemberCheckCatalogView() throws Exception {
		log.info("INFO: Check Community Action Catalog view as Owner");
		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.addMember(new Member(CommunityRole.MEMBERS, memberUser))
											.shareOutside(true) 			// external
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
	
		community.create(ui);
		ui.waitForSameTime();
		ui.logout();
		driver.quit();
		
		log.info("INFO: Check Community Action Catalog view as member");
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(memberUser);

		ui.clickLinkWait(CommunitiesUICloud.memberView);	

		ui.waitForSameTime();
		verifyExternalSetting(community.getName(), "Member View");

		ui.logout();
		driver.quit();
		
		// delete the community as the owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
		
		ui.waitForSameTime();
		ui.openCommunity(community.getName());
		
		// delete it
		ui.delete(community, ownerUser); 
		ui.endTest();
	} 
	
	
	/**
	 * checkExternal() - method to check an External community settings
	 * @param BaseCommunity community
	 * @throws Exception
	 */
	public void checkExternal(BaseCommunity community){

		//Click Start A Community
		log.info("INFO: Check External community using Start A Community button");
		driver.getSingleElement(CommunitiesUIConstants.StartACommunity).click();

		//Wait for Community page to load
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		
		// check External Community Setting
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityallowExternalBox));
		Assert.assertTrue(driver.isTextPresent(CommunitiesUIConstants.AllowExternalTxt));
		
		// verify external setting
		Assert.assertTrue(driver.isTextNotPresent(CommunitiesUIConstants.InternalCommunityMsg));
			
		// un-select it
		driver.getFirstElement(CommunitiesUIConstants.CommunityallowExternalBox).click();
		
		// Cancel the community
		log.info("INFO: Cancelling the community ");	
		ui.fluentWaitPresent(CommunitiesUIConstants.CancelButton);
		driver.getSingleElement(CommunitiesUIConstants.CancelButton).click();
	}
	
	

}
