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
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_VisitorModel_Communities_RequestToJoin extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_Communities_RequestToJoin.class);

	private HomepageUI ui;	
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;	
	private APICommunitiesHandler apiOwner, apiFollower;	
	private String serverURL;		
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser2.getDisplayName()));		
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());		
		apiFollower = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}

	/**
	* visitorModel_standardUser_requestToJoin_modCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 creates a moderated community</B></li>	
	*<li><B>Step: testUser 2 logs on to Communities</B></li>
	*<li><B>Step: testUser 2 requests to join the moderated community</B></li>
	*<li><B>Step: testUser 1 goes to Home/ My Notifications/ For Me</B></li>
	*<li><B>Verify: Action links on the Request to join notification notification having the following order -  1) Add member 2) Save this</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C9AF61626B4DEFF985257C8B004E66A2">TTT -  AS - MY NOTIFICATIONS - FOR ME - 00067 - STANDARD USER - MODERATED COMMUNITY - REQUEST TO JOIN</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_requestToJoin_modCommunity() {		
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.MODERATED);	
		Community newCommunity = baseCommunity.createAPI(apiOwner);	
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity)); 
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now send requests to join the communities");
		String requestToJoinMessage = Helper.genStrongRand();	
		apiFollower.requestToJoinCommunity(newCommunity, requestToJoinMessage);		
		
		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);		
		
		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_REQUEST_FOR_ME, baseCommunity.getName(), null, testUser2.getDisplayName());	
		
		log.info("INFO: Navigate to Ny Notifications");
		ui.gotoMyNotifications();	
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, requestToJoinMessage}, HomepageUIConstants.FilterAll, true);
		
		//HomepageUI.getStatusUpdateMesage method can find news story when testUser's name has been removed
		String newsStoryElement = newsStory.replace(testUser2.getDisplayName(), "");	
		
		boolean found = false;
		String target = "Add memberSave this";
		found = ui.searchForElement(target, HomepageUI.getStatusUpdateMesage(newsStoryElement));
		
		log.info("INFO: Found = " + found);
		
		log.info("INFO: Verify the links appear in this order: 'Add member' 'Save this'");
		Assert.assertTrue(found, "ERROR: Links have NOT been found in the correct order");
		
		log.info("INFO: Delete the community for SmartCloud clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();		
	}
}
