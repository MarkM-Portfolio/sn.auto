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

public class FVT_SCACVModerated extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCACVModerated.class);
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
	
	public void verifyModeratedSetting(String sTitle, String viewName ) throws Exception {

		ui.openCommunity(sTitle);
	
		Assert.assertTrue(driver.isTextPresent(sTitle), "See community at " + viewName);
		log.info("Got this community: " + sTitle + " at " + viewName);
		
		ui.waitForSameTime();
											  
		String moderatedIcon = "css=img[title='This is a moderated community']";
		log.info("INFO: Check for Moderated icon for a moderated community");	
		ui.fluentWaitPresent(moderatedIcon);
		Assert.assertTrue(driver.isElementPresent(moderatedIcon), "Got moderated icon");
		
			// check the member section
			ui.fluentWaitPresent(CommunitiesUICloud.memberLink);
			driver.getSingleElement(CommunitiesUICloud.memberLink).click();
		
			String text = "This community cannot have members from outside your organization.";
			ui.fluentWaitTextPresent(text);
			Assert.assertTrue(driver.isTextPresent(text), "Got message: " + text);
			log.info("INFO: Pass checking");
		
		if (  viewName.equals("Owner view")) {
			
			String MemberMederatedIcon = "css=span img[title='Moderated']";
			ui.fluentWaitPresent(MemberMederatedIcon);
			Assert.assertTrue(driver.isElementPresent(MemberMederatedIcon));
		
			String AddIconNone = "This community cannot have members from outside your organization.";
			ui.fluentWaitTextPresent(AddIconNone);
			Assert.assertTrue(driver.isTextPresent(AddIconNone));
		}
		
		log.info("INFO: Pass checking");		
	} 
	
	/**
	 * checkExternal() - method to check an External community settings
	 * @param BaseCommunity community
	 * @throws Exception
	 */
	public void checkModerated(BaseCommunity community){

		String ModeratedMsg = "Once a community is designated as solely for your organization, "
				+ "it can never be changed to allow people from outside your organization.";
				
		//Click Start A Community
		log.info("INFO: Check moderated community using Start A Community button");
		
		ui.waitForSameTime();
		ui.fluentWaitPresent(CommunitiesUIConstants.StartACommunity);
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunity).click();

		//Wait for Community page to load
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		
		// setup for moderated
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityAccessModerated));
		driver.getFirstElement(CommunitiesUIConstants.CommunityAccessModerated).click();
		
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
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Login as a user with testing company setting for an external community</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Verify:</B>Check the catalog view as an owner</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *<li><B>Verify:</B>Check the community has been deleted</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
//	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ModeratedView() throws Exception {

		log.info("INFO: Check Community Action Catalog view as Owner");
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
		ui.login(ownerUser);
	
		ui.waitForSameTime();
		checkModerated(community);
		
		community.create(ui);
		
		verifyModeratedSetting(community.getName(), "Owner View");
		
		ui.delete(community, ownerUser);
		
		ui.logout();
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Login as a user with testing company setting for an external community</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Verify:</B>Check the catalog view as a member</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *<li><B>Verify:</B>Check the community is deleted</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
//	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void MemberCheckCatalogView() throws Exception {

		log.info(" ***************** INFO: Check Community Action Catalog view as member *************");
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
		
		//Load component and login as owner to create a community with a member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
	
		ui.waitForSameTime();
		
		community.create(ui);
		ui.waitForSameTime();
		ui.logout();
		driver.quit();
		
		//Load component and login as member to verify it
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(memberUser);

		ui.waitForSameTime();
		ui.clickLinkWait(CommunitiesUICloud.memberView);	
		verifyModeratedSetting(community.getName(), "Member View");
		ui.logout();
		driver.quit();
		
		//Load component and login as owner to delete the community
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
		ui.waitForSameTime();
		
		ui.openCommunity(community.getName());
		ui.delete(community, ownerUser);
		
		ui.logout();
		ui.endTest();
	} 
	
	/**
	 *<ul>
	 *<li><B>Info: Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Login as a user with testing company setting for an external community</li>
	 *<li><B>Step:</B>Create an internal restricted community</li>
	 *<li><B>Verify:</B>Check the catalog view as a following</li>
	 *<li><B>Step:</B>Delete the community</li>
	 *<li><B>Verify:</B>Check the community has been deleted</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void OwnerCheckFolliwView() throws Exception {

		log.info("INFO: Check Community Action Catalog view as following");
	
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
		ui.login(ownerUser);

		ui.waitForSameTime();		
		community.create(ui);	
		ui.waitForSameTime();
	
		ui.logout();
		driver.quit();
		
		// login back to chek the following view
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.waitForSameTime();		
		ui.clickLinkWait(CommunitiesUICloud.FollowView);	
		
		ui.waitForSameTime();
		verifyModeratedSetting(community.getName(), "Following View");
		
		// delete the top one
		ui.delete(community, ownerUser);
	
		ui.logout(); 
		
		ui.endTest();
	}  
}
