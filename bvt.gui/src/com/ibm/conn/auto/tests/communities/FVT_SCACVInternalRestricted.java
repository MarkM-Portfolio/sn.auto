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

public class FVT_SCACVInternalRestricted extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCACVInternalRestricted.class);
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
	
	public void verifyInternalSetting(String sTitle, String viewName ) throws Exception {

		ui.openCommunity(sTitle);
	
		Assert.assertTrue(driver.isTextPresent(sTitle), "See community at " + viewName);
		log.info("Got this community: " + sTitle + " at " + viewName);
		
		ui.waitForSameTime();
		
		try {
			log.info("INFO: Check no external icon for internal community");	
			Assert.assertFalse(driver.isElementPresent(CommunitiesUICloud.externalIcon), "No external icon");
			
		} catch (Exception e) {
			log.info("INFO: There is no external icon for internal community");	
		}
		
		Assert.assertTrue(driver.isTextNotPresent(CommunitiesUICloud.externalMsg), "There is no external message");
		log.info("No external community message for internal community");
		
		// check the member section
		ui.fluentWaitPresent(CommunitiesUICloud.memberLink);
		driver.getSingleElement(CommunitiesUICloud.memberLink).click();
		
		ui.fluentWaitTextPresent(CommunitiesUICloud.internalMemberMsg);
		Assert.assertTrue(driver.isTextPresent(CommunitiesUICloud.internalMemberMsg), 
							"Got message: " + CommunitiesUICloud.internalMemberMsg);
		
		if ( viewName.equals("Owner view")) {
			// Add member no external add button
			ui.fluentWaitPresent(CommunitiesUICloud.AddMemberLink);
			driver.getSingleElement(CommunitiesUICloud.AddMemberLink).click();
		
			ui.fluentWaitPresent(CommunitiesUICloud.AddIconNone);
			Assert.assertTrue(driver.isElementPresent(CommunitiesUICloud.AddIconNone));
		}
	} 
	

	/**
	 *<ul>
	 *<li><B>Info:</B> Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Login as a user with testing company setting for an internal community.</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Verify:</B>Check the catalog view as an owner</li>
	 *<li><B>Verify:</B>Check the catalog view as a member</li>
	 *<li><B>Verify:</B>Check the catalog view as a following</li>
	 *<li><B>Step:</B>Delete the community.</li>
	 *<li><B>Verify:</B>Check that there is no external community message for internal communityq</li>
	 *</ul>
	 *@author Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void InternalRestrictedView() throws Exception {

		log.info("INFO: Check InternalRestricted Community Action Catalog view as Owner");
		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();

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
		ui.login(ownerUser);
		
		// check for internal community
		ui.checkInternal(community);		
		community.create(ui);
		
		verifyInternalSetting(community.getName(), "Owner View");
		
		ui.logout();
		driver.quit();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
	
		log.info("INFO: Check Community Action Catalog view as following");
		ui.clickLinkWait( CommunitiesUICloud.FollowView);	
		
		ui.waitForSameTime();
		verifyInternalSetting(community.getName(), "Following View");
			
		// delete the top one
		ui.delete(community, ownerUser);
	
		ui.logout();
		ui.endTest();
	} 

}
