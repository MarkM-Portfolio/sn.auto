package com.ibm.conn.auto.tests.communities.regression;

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
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class Regression_CheckNoModerated extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Regression_CheckNoModerated.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	
	private BaseCommunity community;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);		
	}
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load User
		testUser = 	cfg.getUserAllocator().getGroupUser("policy1");
		log.info("INFO: Using test user: " + testUser.getDisplayName());

	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Info: Moderation needs to be setup on a separate server - This is a multi step test</B></li>
	 *<li><B>Steps: 
	 * Need users which have policy settings to run this test
	 * Login as a user with testing company setting for the No Moderated community.
	 * Create a no moderated community.
	 * Enable sub community.
	 * Create a no moderated sub community.
	 * delete the no moderated sub community.
	 * delete the no moderated community.
	 * Logout.
	 * 
	</B> </li>
	 *<li><B>Verify: </B> </li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"cloudregression"})
	public void CheckNoModeratedCommunity() throws Exception {

		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create a moderated community base state object
		community = new BaseCommunity.Builder("CheckNoModerated" + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.build();
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
	
		// check a no moderated community
		checkCommunityAccess(true);
		
		community.create(ui);
		
		// Enable SubCommunity
		ui.addWidget(BaseWidget.SUBCOMMUNITIES);
			
		// check a no moderated sub community
		checkSubCommunityAccess(true);
		
		// delete the top one
		ui.delete(community, testUser); 
		ui.logout();
		
		ui.endTest();
	}
	

	/**
	 * create() - method to create an community using a community base state object
	 * @param BaseCommunity community
	 * @throws Exception
	 */
	public void checkCommunityAccess(boolean NoModerated){
	
		log.info("INFO: Check the community access with No Moderated = " + NoModerated);
		
		//Click Start A Community
		log.info("INFO: Create a new community using Start A Community button");
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunity).click();

		//Wait for Community page to load
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
	
		// check Restricted
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityAccessPrivate);
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityAccessPrivate));
		Assert.assertTrue(driver.isTextPresent(Data.getData().RestrictedTxt));
		
		// Check Allow people from outside
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityallowExternalBox));
		Assert.assertTrue(driver.isTextPresent(Data.getData().AllowExternalTxt));
		
		// Check No Moderated Set		
		if ( NoModerated ) {
			Assert.assertTrue( driver.isTextNotPresent(Data.getData().ModeratedTxt));
		}
		else
		{
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityAccessModerated));
			Assert.assertTrue(driver.isTextPresent(Data.getData().AddPublicTxt));
		}
		
		// Check public access
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityAccessPublic));
		Assert.assertTrue(driver.isTextPresent(Data.getData().addPublicAccessTxt));
	
		// Cancel the community
		log.info("INFO: Cancelling the community ");	
		this.driver.getSingleElement(CommunitiesUIConstants.CancelButton).click();
		
	}
	
	
	/**
	 * create() - method to create an community using a community base state object
	 * @param BaseCommunity community
	 * @throws Exception
	 */
	public void checkSubCommunityAccess(boolean NoModerated){
	
		log.info("INFO: Check sub community access with No Moderated = " + NoModerated);
		
		try {
			Com_Action_Menu.CREATESUB.select(ui);
		} catch (Exception e) {
			log.info("ERROR: Unable to use the community action menu properly to create subcommunity");
			e.printStackTrace();
		}

		//Wait for Community page to load
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
	
		// check Restricted
		ui.fluentWaitPresent(CommunitiesUIConstants.SubCommunityAccessPrivate);
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.SubCommunityAccessPrivate));
		Assert.assertTrue(driver.isTextPresent(Data.getData().RestrictedTxt));
		
		// Check No Moderated Set		
		if ( NoModerated ) {
			Assert.assertTrue( driver.isTextNotPresent(Data.getData().ModeratedTxt));
		}
		
		// Check public access
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.SubCommunityAccessPublic));
		Assert.assertTrue(driver.isTextPresent(Data.getData().addPublicAccessTxt));
	
		// Cancel the community
		log.info("INFO: Cancelling the community ");	
		ui.fluentWaitPresent(CommunitiesUIConstants.SubCommunityCancelButton);
		driver.getSingleElement(CommunitiesUIConstants.SubCommunityCancelButton).click();
		
	}
	
	
}
