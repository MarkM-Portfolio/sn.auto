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
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;

public class FVT_SCACVOpenCommunity extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCACVOpenCommunity.class);
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

		log.info("INFO: Using onwer user: " + ownerUser.getDisplayName());
		log.info("INFO: Using member user: " + memberUser.getDisplayName());
	}
	
	public void verifyOpenCommunity(String sTitle, String viewName ) throws Exception {

		ui.openCommunity(sTitle);
	
		Assert.assertTrue(driver.isTextPresent(sTitle), "See community at " + viewName);
		log.info("Got this community: " + sTitle + " at " + viewName);
		
		ui.waitForSameTime();
											  	
		// check the member section
		ui.fluentWaitPresent(CommunitiesUICloud.memberLink);
		driver.getSingleElement(CommunitiesUICloud.memberLink).click();
		
		String text = "This community cannot have members from outside your organization.";
		ui.fluentWaitTextPresent(text);
		Assert.assertTrue(driver.isTextPresent(text), "Got message: " + text);
	} 
	
	/**
	 * checkExternal() - method to check an Open community settings
	 * @param BaseCommunity community
	 * @throws Exception
	 */
	public void checkOpenCommunity(BaseCommunity community){

		String ModeratedMsg = "Once a community is designated as solely for your organization, "
				+ "it can never be changed to allow people from outside your organization.";
				
		//Click Start A Community
		log.info("INFO: Check moderated community using Start A Community button");
		
		ui.waitForSameTime();
		ui.fluentWaitPresent(CommunitiesUIConstants.StartACommunity);
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunity).click();

		//Wait for Community page to load
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		
		// setup for open community
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityAccessPublic));
		driver.getFirstElement(CommunitiesUIConstants.CommunityAccessPublic).click();
		
		// verify warning message
		String warningIcon = "css=img[class='lotusIcon lotusIconMsgWarning']";
		ui.fluentWaitPresent(warningIcon);
		Assert.assertTrue(driver.isElementPresent(warningIcon));		
		Assert.assertTrue(driver.isTextPresent(ModeratedMsg)); 
	
		// verify no + plus icon
		Assert.assertTrue(driver.isElementPresent(CommunitiesUICloud.AddIconNone));
		
		// Cancel the community
		log.info("INFO: Cancelling the community ");	
		ui.fluentWaitPresent(CommunitiesUIConstants.CancelButton);
		this.driver.getSingleElement(CommunitiesUIConstants.CancelButton).click();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Info: Check the community catalog view for an External Restriced community </B></li>
	 *<li><B>Steps: 
	 * Login as a user with testing company setting for an public community.
	 * Create open community
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
	public void OpenCommunity() throws Exception {

		log.info("INFO: Check Open Community Action Catalog view as Owner");
		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.access(Access.PUBLIC)
											.build();  
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
	
		checkOpenCommunity(community);

		community.create(ui);
		
		verifyOpenCommunity(community.getName(), "Owner View");
		
		ui.logout();
		driver.quit();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.clickLinkWait(CommunitiesUICloud.FollowView);	
		
		ui.waitForSameTime();
		verifyOpenCommunity(community.getName(), "Following View");
		
		// delete the top one
		ui.openCommunity(community.getName());
		ui.delete(community, ownerUser);
	
		ui.logout();
		
		ui.endTest();
	} 

}
