/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2013 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.communities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cloud.ActivitiesUICloud;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;
import com.ibm.conn.auto.webui.onprem.ActivitiesUIOnPrem;
import com.ibm.conn.auto.webui.onprem.CommunitiesUIOnPrem;


public class BVT_Level_2_Communities_CCH extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities_Library.class);
	private CommunitiesUI uiComm;
	private ActivitiesUI uiAct;
	private TestConfigCustom cfg;
	
	private BaseCommunity community;
	private BaseCommunity subcommunity;
	private BaseActivity activity;
	
	private String communityName;
	private String subcommunityName;
	
	Boolean Bluepage_Users_Required = true;
	private User testUser1, testUser2, testUser3;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		log.info("INFO: setting up test: testMoveActivity");

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();

		String product = cfg.getProductName();
		if(product.toLowerCase().equals("cloud")){
			uiComm = new CommunitiesUICloud(driver);
			uiAct = new ActivitiesUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			uiComm = new CommunitiesUIOnPrem(driver);
			uiAct = new ActivitiesUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
		
		initialize();
	}								 

	private void initialize() throws Exception{
		log.info("INFO: initialize");
		
		String rndNum = Helper.genDateBasedRand();
		//Create a community base state object
		communityName = Data.getData().commonName + rndNum;
		community = new BaseCommunity.Builder(communityName)
									 .commHandle(Data.getData().commonHandle + rndNum)
									 .description("Test CCH features.").build();

		//Create a sub-community base state object
		subcommunityName = Data.getData().commonName + "_subcomm_" + rndNum;
		subcommunity = new BaseCommunity.Builder(subcommunityName)
									 .commHandle(Data.getData().commonHandle + rndNum)
									 .description("Test CCH features subcommunity.").build();

		//Create an activity base state object
		activity = new BaseActivity.Builder(Data.getData().Start_An_Activity_InputText_Name_Data + rndNum).build();
		
		//Create user objects
		testUser1 = cfg.getUserAllocator().getUser();	
		testUser2 = cfg.getUserAllocator().getGroupUser("global_editor",this);
		testUser3 = cfg.getUserAllocator().getGroupUser("global_editor",this);
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Info: Moderation needs to be setup on a separate server - This is a multi step test</B></li>
	 *<li><B>Steps: 
	 *
	 * Test case:  Move a community activity to a sub-community
	 * 
	 * 1. log in as user A, create a community A and add user B C as members.
	 * 2. customize community A by adding the Activities widget.
	 * 3. create an activity A in community A, using implicit membership, so that both User B and C will be author of activity A.
	 * 4. create a sub-community B in community A, add User B as member, not User C.
	 * 5. move activity A from community A to sub-community B.
	 * 6. verify the moving is successful with no error messages.
	 * 7. verify user A has access to activity A.
	 * 8. verify user B and C has access to activity A.
	 * 
	 *<li><B>Verify: </B> </li>
	 *</ul>
	 */
	@Test(groups = { "bvt", "level2" }, dependsOnGroups = {})
	public void testMoveActivity() throws Exception {

		log.info("INFO: starting test: testMoveActivity");
		uiComm.startTest();
		
		log.info("INFO: loading component: communities");
		uiComm.loadComponent(Data.getData().ComponentCommunities);

		log.info("INFO: Using test user: " + testUser1.getDisplayName());
		uiComm.login(testUser1);
		
		createCommunity();
		addUserToCommunity(testUser2);
		addUserToCommunity(testUser3);
		addActivitiesWidget(community);
		createActivity();
		
		createSubCommunity();
		addUserToCommunity(testUser2);
		addActivitiesWidget(subcommunity);

		moveActivity();
		
		verifyMoveActivity();
		verifyUsersInSubCommunity();
		
		log.info("INFO: Logout and quit browser");
		uiComm.logout();
		
		log.info("INFO: ending test: testMoveActivity");	
		uiComm.endTest();
	}
	

	/* 1. log in as user A, create a community A and add user B C as members. */
	private void createCommunity()throws Exception{
		log.info("INFO: createCommunity");
		community.create(uiComm);
	}		

	private void addUserToCommunity(User user) throws Exception{
		log.info("INFO: addUserToCommunity");
		log.info("INFO: Add user ("+user.getDisplayName()+")");
		uiComm.addMemberCommunity(new Member(CommunityRole.MEMBERS, user));
	}
	
	/* 2. customize community A by adding the Activities widget.              */
	private void addActivitiesWidget(BaseCommunity comm) throws Exception{
		log.info("INFO: addActivitiesWidget");
		log.info("INFO: Adding the " + BaseWidget.ACTIVITIES.getTitle() + " widget to community: "+ comm.getName());
		uiComm.addWidget(BaseWidget.ACTIVITIES);
	}
	
	/* 3. create an activity in community, using implicit membership,         */
	/* so that both User B and C will be author of activity A.                */
	private void createActivity()throws Exception{
		log.info("INFO: createActivity");
		uiComm.clickCreateCommunityActivityButton();
		activity.create(uiAct); // by default, members of the community are added implicitly to the activity upon creation
	}
		
	/* 4. create a sub-community in community, add User B as member, NOT User C.  */
	private void createSubCommunity()throws Exception{
		log.info("INFO: createSubCommunity");
		Com_Action_Menu.CREATESUB.select(uiComm);
		
		log.info("INFO: Create subcommunity");
		uiComm.create(subcommunity);
	}		

	/* 5. move activity from community to sub-community                       */
	private void moveActivity()throws Exception{
		log.info("INFO: moveActivity");

		uiComm.gotoCommunityFromSubcommunity(communityName);
		
		log.info("INFO: Select Activities from the left navigation menu");
		Community_LeftNav_Menu.ACTIVITIES.select(uiComm);	
		uiComm.gotoActivity(activity.getName());
		uiComm.clickActivityMenuMoveActivity();
		uiComm.clickMoveActivitySelectCommunity(subcommunity.getName());
		uiComm.clickMoveActivitySelectAllMembers();
		uiComm.clickMoveActivityOkButton();
	}
		
	/* 6. verify the moving is successful with no error messages.             */
	private void verifyMoveActivity()throws Exception{
		log.info("INFO: verifyMoveActivity");
		
		// check for success message
		uiComm.fluentWaitTextPresent("Create Your First Activity"); // since moving the only activity, this button text should exist after the move occurs
		Assert.assertTrue(driver.isTextPresent("\""+activity.getName()+"\" was moved successfully"), "Expected: "+"\""+activity.getName()+"\" was moved successfully");
		
		uiComm.gotoSubCommunity(subcommunity.getName());
		uiComm.fluentWaitTextPresent("div.lotusPlaceBar .bidiAware:contains("+subcommunity.getName()+")");

		log.info("INFO: Select Activities from the left navigation menu");
		Community_LeftNav_Menu.ACTIVITIES.select(uiComm);	
		
		// verify activity is loaded in sub community
		Assert.assertTrue(driver.isTextPresent(activity.getName()), "Expected: "+activity.getName());
	}
		
	/* 7. verify user A has access to activity A.                             */
	private void verifyUsersInSubCommunity()throws Exception{
		log.info("INFO: verifyUsersInSubCommunity");
		log.info("INFO: verify user1 has activity access in sub-community");
		
		uiComm.gotoSubCommunity(subcommunity.getName());
		uiComm.gotoSubCommunityAllMembers();
		
		// verify user 1 exists in sub community
		log.info("INFO: verify user1 ("+testUser1.getDisplayName()+") has access in sub-community");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName()), "Expected: "+testUser1.getDisplayName());
		
		
		/**************************************************************************/
		/* 8. verify user B and C have access to activity A.                        */
		log.info("INFO: verify user2 ("+testUser2.getDisplayName()+") & user3 ("+testUser3.getDisplayName()+") have activity access in sub-community");
		Assert.assertTrue(driver.isTextPresent(testUser2.getDisplayName()), "Expected: "+testUser2.getDisplayName());
		Assert.assertTrue(driver.isTextPresent(testUser3.getDisplayName()), "Expected: "+testUser3.getDisplayName());
	}	
}
