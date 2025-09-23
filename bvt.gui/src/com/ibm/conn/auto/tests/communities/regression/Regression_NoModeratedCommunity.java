package com.ibm.conn.auto.tests.communities.regression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
// import com.ibm.conn.auto.appobjects.Widget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class Regression_NoModeratedCommunity extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Regression_NoModeratedCommunity.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	
	private BaseCommunity community;
	private BaseSubCommunity subCommunity;
	
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
	public void NoModeratedCommunity() throws Exception {

		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create a moderated community base state object
		community = new BaseCommunity.Builder("NoModerated" + date)
											.tags(Data.getData().commonTag + date)
											.description(Data.getData().commonDescription)
											.build();
		subCommunity = new BaseSubCommunity.Builder("SubNoModerated" + date)
			.tags(Data.getData().commonTag + date)
			.description(Data.getData().commonDescription)
			.build();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
	
		// create a no moderated community
		community.create(ui);
		
		// Enable SubCommunity
		ui.addWidget(BaseWidget.SUBCOMMUNITIES);
		
		// create a no moderated sub community
		subCommunity.create(ui);
			
		// delete a sub community 	
		subCommunity.delete(ui, testUser);
		
		// Open the top community
		ui.openCommunityLink(community);
		
		// delete the top one
		ui.delete(community, testUser);
		ui.logout();
		ui.endTest();
	}
}
