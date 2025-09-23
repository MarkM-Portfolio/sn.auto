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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_VisitorModel_Communities_SharedExternally_MemberAdded extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_Communities_SharedExternally_MemberAdded.class);

	private HomepageUI ui;	
	private TestConfigCustom cfg;	
	private User testUser1, testUser3;	
	private APICommunitiesHandler apiOwner;	
	private String serverURL;		
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);			

		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));
				
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}

	/**
	* visitorModel_standardUser_sharedExternallyHeader_privateCommunity_memberAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 creates a private community that can have visitors as members</B></li>
	*<li><B>Step: testUser 1 adds testUser2 to the community</B></li>
	*<li><B>Step: testUser 3 goes to Home/ My Notifications/ For Me / All</B></li>
	*<li><B>Step: testUser 3 goes to the story of the community membership</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title of the community membership</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AD8EBE6A718B350985257C8C0038673F">TTT - VISITORS - ACTIVITY STREAM - 00025 - SHARED EXTERNALLY HEADER - STANDARD USER - ADDED TO COMMUNITY- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_sharedExternallyHeader_privateCommunity_memberAdded() {
		
		String testName = ui.startTest();
		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = baseCommunity.createAPI(apiOwner);	
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity)); 
		
		log.info("INFO: " + testUser3.getDisplayName() + " Adding " + testUser1.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);	
		
		log.info("INFO: Logging in with " + testUser3.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, baseCommunity.getName(), null, testUser1.getDisplayName());
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Navigate to My Notifications");
		ui.gotoMyNotifications();		
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, HomepageUIConstants.FilterAll, true);
		
		log.info("INFO: Delete the community for SmartCloud clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}
}
