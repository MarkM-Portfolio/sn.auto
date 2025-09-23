package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_VisitorModel_Communities_SharedExternally_Invited extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_Communities_SharedExternally_Invited.class);

	private HomepageUI ui;	
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;	
	private APICommunitiesHandler apiOwner;
	private APIProfilesHandler testUser2Profile;
	private String serverURL;		
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);		

		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());		
		testUser2Profile = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());		
	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}

	/**
	* visitorModel_standardUser_sharedExternallyHeader_privateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community that can have visitors as members</B></li>
	*<li><B>Step: testUser1 invites testUser2 to the community</B></li>
	*<li><B>Step: testUser2 goes to Home/ My Notifications/ For Me / All</B></li>
	*<li><B>Step: testUser2 goes to the story of the community membership</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title of the community membership</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0A1251D36A79550585257C8C0035EC90">TTT - VISITORS - ACTIVITY STREAM - 00022 - SHARED EXTERNALLY HEADER - STANDARD USER - INVITED TO COMMUNITY- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_sharedExternallyHeader_privateCommunity(){
		
		String testName = ui.startTest();
		
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
								   .tags(Data.getData().commonTag + Helper.genStrongRand())
								   .access(Access.RESTRICTED)
								   .shareOutside(true)
								   .description(Data.getData().commonDescription + Helper.genStrongRand())
								   .build();

		log.info("INFO: " + testUser1.getDisplayName() + " creating Community " + baseCom.getName() + " using API method");
		Community newCommunity = baseCom.createAPI(apiOwner);

		log.info("INFO: Set UUID of community");
		baseCom.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity)); 	
		
		log.info("INFO: Inviting" + testUser2.getDisplayName() + "  to join the Public community");	
		apiOwner.inviteUserToJoinCommunity(newCommunity, testUser2Profile);		
		
		log.info("INFO: Logging in with " + testUser2.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		
		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_FOR_ME, baseCom.getName(), null, testUser1.getDisplayName());	
		String newsStoryElement = newsStory.replace(testUser1.getDisplayName(), "");
		
		log.info("INFO: Navigate to My Notifications");
		ui.gotoMyNotifications();
		
		//Clicking the 'Show more' link to make the test case more robust by ensuring the news story has NOT been pushed off the page
		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		log.info("INFO: Verify that the NewsStory is appearing in MyNotifications/All");
		Assert.assertTrue(ui.fluentWaitTextPresent(newsStory),
				 "ERROR: NewsStory is does NOT appearing in MyNotifications/All");	
		
		log.info("INFO: Verify that the 'Shared Externally' message and icon are displayed with the community invitation in MyNotifications/All");
		Assert.assertTrue(driver.isTextPresent(Data.SharedExternallyMsg),
						 "ERROR: 'Shared Externally' message is NOT displayed with the community invitation");
		
		log.info("INFO: Verify that the 'Shared Externally_Warning' icon in MyNotifications/All");
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(newsStoryElement), HomepageUIConstants.SharedExternally_Warning);
		
		log.info("INFO: Delete the community for SmartCloud clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();		
	}
}

