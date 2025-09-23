package com.ibm.conn.auto.sandbox.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ibm.conn.auto.util.OrgConfig;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class multiTenant extends SetUpMethods2{

	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private OrgConfig org1;
	private OrgConfig org2;
	
	
	private User testUser, testLookAheadUser;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		org1 = orgs.get(0);
		org2 = orgs.get(1);
		
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();		
		
		//reset organization back to server_url
		cfg.resetOrg();	
	}	
	
	/**
	*<ul>
	*<li><B>Info: /B></li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvtcloud"})
	public void createPublicCommunity() throws Exception {


		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();

		log.info("INFO: Base URL: " + cfg.getTestConfig().getBrowserURL());
		
		log.info("INFO: Server URL: " + cfg.getServerURL());
		
		//switch to new org
		cfg.switchToOrg(org1);	
		log.info("INFO: Orgname for config1: " + org1.getURI());
		
		log.info("INFO: Server URL: " + cfg.getServerURL());
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		community.create(ui);
		
		//Logout of Communities
		ui.logout();
		ui.close(cfg);


		//switch to new org
		cfg.switchToOrg(org2);		
		log.info("INFO: Orgname for config2: " + org2.getURI());
		

		log.info("INFO: Server URL: " + cfg.getServerURL());
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		community.create(ui);
		
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: /B></li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvtcloud"})
	public void createPublicCommunity2() throws Exception {


		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();

		log.info("INFO: Base URL: " + cfg.getTestConfig().getBrowserURL());		
		log.info("INFO: Server URL: " + cfg.getServerURL());	
		log.info("INFO: Server URL: " + cfg.getServerURL());
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		community.create(ui);
		
		ui.endTest();
	}

	
}
